using System;
using System.Collections.Generic;
using System.Net.Sockets;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading;
using System.Threading.Tasks;

namespace Lab5
{
    public class StateObject
    {
        public Socket workSocket = null;
        public const int BUFFER_SIZE = 10240;
        public byte[] buffer = new byte[BUFFER_SIZE];
        public StringBuilder sb = new StringBuilder();
    }

    class Program
    {
        private static string host;
        private static int port;
        private static readonly List<string> paths = new List<string>();
        public static ManualResetEvent allDone = new ManualResetEvent(false);
        private Object thisLock = new Object();

        static void Main()
        {
            InitHost();
            //MethodOne();
            //MethodTwo();
            //allDone.WaitOne();
            MethodThree();
            allDone.WaitOne();
        }

        private async static void MethodThree()
        {
            allDone.Reset();
            foreach (string path in paths)
            {
                var connectResult = await Task.Factory.StartNew(() =>
                {
                    TaskCompletionSource<IAsyncResult> res = new TaskCompletionSource<IAsyncResult>();
                    Socket socket = new Socket(AddressFamily.InterNetwork,
                                          SocketType.Stream,
                                          ProtocolType.Tcp);
                    socket.BeginConnect(host, port, (ar) =>
                    {
                        res.SetResult(ar);
                    }, socket);

                    return res.Task.Result;
                });
                var sendResult = await Task.Factory.StartNew(() =>
                {
                    Socket socket = (Socket)connectResult.AsyncState;
                    socket.EndConnect(connectResult);
                    StateObject so2 = new StateObject();
                    so2.workSocket = socket;
                    byte[] request = GetRequest(path);

                    TaskCompletionSource<IAsyncResult> res2 = new TaskCompletionSource<IAsyncResult>();
                    socket.BeginSend(request, 0, request.Length, 0, (ar) =>
                    {
                        res2.SetResult(ar);
                    }, so2);

                    return res2.Task.Result;
                });
                var receiveResult = await Task.Factory.StartNew(() =>
                {
                    Socket socket = ((StateObject)sendResult.AsyncState).workSocket;
                    socket.EndSend(sendResult);

                    StateObject so = new StateObject
                    {
                        workSocket = socket
                    };

                    TaskCompletionSource<IAsyncResult> res2 = new TaskCompletionSource<IAsyncResult>();
                    socket.BeginReceive(so.buffer, 0, StateObject.BUFFER_SIZE, 0,
                                        (ar) =>
                                        {
                                            res2.SetResult(ar);
                                        }, so);

                    return res2.Task.Result;
                });
                var data = await Task.Factory.StartNew(() => {
                    return ReceiveDataMethodThree(receiveResult);
                });

                processResponse(data);
            }
            allDone.Set();
        }

        private static string ReceiveDataMethodThree(IAsyncResult receiveResult)
        {
            StateObject so = (StateObject)receiveResult.AsyncState;
            Socket s = so.workSocket;

            int read = s.EndReceive(receiveResult);

            if (read > 0)
            {
                TaskCompletionSource<IAsyncResult> res2 = new TaskCompletionSource<IAsyncResult>();
                so.sb.Append(Encoding.ASCII.GetString(so.buffer, 0, read));
                s.BeginReceive(so.buffer, 0, StateObject.BUFFER_SIZE, 0, (ar) => {
                    res2.SetResult(ar);
                }, so);

                return ReceiveDataMethodThree(res2.Task.Result);
            }
            else
            {
                if (so.sb.Length > 1)
                {
                    //All of the data has been read, so displays it to the console
                    string strContent;
                    strContent = so.sb.ToString();
                    Console.WriteLine(String.Format("Read {0} byte from socket", strContent.Length));

                    return strContent;
                }
                s.Close();
                return "";
            }
        }

        private static void MethodTwo()
        {
            allDone.Reset();
            foreach (string path in paths)
            {
                var data = Task.Factory.StartNew(() =>
                {
                    TaskCompletionSource<IAsyncResult> res = new TaskCompletionSource<IAsyncResult>();
                    Socket socket = new Socket(AddressFamily.InterNetwork,
                                          SocketType.Stream,
                                          ProtocolType.Tcp);
                    socket.BeginConnect(host, port, (ar) => {
                        res.SetResult(ar);
                    }, socket);

                    return res.Task.Result;
                }).ContinueWith((obj) => {
                    IAsyncResult res = obj.Result;
                    Socket socket = (Socket)res.AsyncState;
                    socket.EndConnect(res);
                    StateObject so2 = new StateObject();
                    so2.workSocket = socket;
                    byte[] request = GetRequest(path);

                    TaskCompletionSource<IAsyncResult> res2 = new TaskCompletionSource<IAsyncResult>();
                    socket.BeginSend(request, 0, request.Length, 0, (ar) => {
                        res2.SetResult(ar);
                    }, so2);

                    return res2.Task.Result;
                }).ContinueWith((obj) => {
                    IAsyncResult res = obj.Result;
                    Socket socket = ((StateObject)res.AsyncState).workSocket;
                    socket.EndSend(res);

                    StateObject so = new StateObject
                    {
                        workSocket = socket
                    };

                    TaskCompletionSource<IAsyncResult> res2 = new TaskCompletionSource<IAsyncResult>();
                    socket.BeginReceive(so.buffer, 0, StateObject.BUFFER_SIZE, 0,
                                        (ar) => {
                        res2.SetResult(ar);
                    }, so);

                    return res2.Task.Result;
                }).ContinueWith((obj) => {
                    return ReceiveData(obj);
                });

                processResponse(data.Result);
            }
            allDone.Set();
        }

        private static string ReceiveData(Task<IAsyncResult> obj)
        {
            IAsyncResult res = obj.Result;
            StateObject so = (StateObject)res.AsyncState;
            Socket s = so.workSocket;

            int read = s.EndReceive(res);

            if (read > 0)
            {
                TaskCompletionSource<IAsyncResult> res2 = new TaskCompletionSource<IAsyncResult>();
                so.sb.Append(Encoding.ASCII.GetString(so.buffer, 0, read));
                s.BeginReceive(so.buffer, 0, StateObject.BUFFER_SIZE, 0, (ar) => {
                    res2.SetResult(ar);
                }, so);

                return ReceiveData(res2.Task);
            }
            else
            {
                if (so.sb.Length > 1)
                {
                    //All of the data has been read, so displays it to the console
                    string strContent;
                    strContent = so.sb.ToString();
                    Console.WriteLine(String.Format("Read {0} byte from socket", strContent.Length));

                    return strContent;
                }
                s.Close();
                return "";
            }
        }

        private static void MethodOne()
        {
            foreach (string path in paths)
            {
                Socket socket = new Socket(AddressFamily.InterNetwork,
                                          SocketType.Stream,
                                          ProtocolType.Tcp);

                allDone.Reset();
                socket.BeginConnect(host, port, (ar) => {
                    ConnectCallback(ar, path);
                }, socket);
                allDone.WaitOne();
            }
        }

        private static void ConnectCallback(IAsyncResult ar, string path)
        {
            Socket socket = (Socket)ar.AsyncState;
            socket.EndConnect(ar);
            StateObject so2 = new StateObject();
            so2.workSocket = socket;
            byte[] request = GetRequest(path);

            socket.BeginSend(request, 0, request.Length, 0, new AsyncCallback(SendCallback), so2);
        }

        private static void SendCallback(IAsyncResult ar)
        {
            Socket socket = ((StateObject)ar.AsyncState).workSocket;
            socket.EndSend(ar);

            StateObject so = new StateObject
            {
                workSocket = socket
            };

            socket.BeginReceive(so.buffer, 0, StateObject.BUFFER_SIZE, 0, 
                                new AsyncCallback(ReceiveCallback), so);
        }

        private static void ReceiveCallback(IAsyncResult ar)
        {
            StateObject so = (StateObject)ar.AsyncState;
            Socket s = so.workSocket;

            int read = s.EndReceive(ar);

            if (read > 0)
            {
                so.sb.Append(Encoding.ASCII.GetString(so.buffer, 0, read));
                s.BeginReceive(so.buffer, 0, StateObject.BUFFER_SIZE, 0,
                               new AsyncCallback(ReceiveCallback), so);
            }
            else
            {
                if (so.sb.Length > 1)
                {
                    //All of the data has been read, so displays it to the console
                    string strContent;
                    strContent = so.sb.ToString();
                    Console.WriteLine(String.Format("Read {0} byte from socket", strContent.Length));

                    processResponse(strContent);

                    allDone.Set();
                }
                s.Close();
            }
        }

        private static void processResponse(string strContent)
        {
            String headersPattern = @"^([^<])*";
            Regex headersRegex = new Regex(headersPattern);

            String contentLengthPattern = @"(Content-Length:\s*)(\d+)";
            Regex contentLengthRegex = new Regex(contentLengthPattern);

            Match headersMatch = headersRegex.Match(strContent);
            Match contentLengthMatch = contentLengthRegex.Match(strContent);

            if (headersMatch.Success) {
                String headers = headersMatch.Groups[(string) headersRegex.GetGroupNames().GetValue(0)].Value.Trim();
                String contentLength = contentLengthMatch.Groups[(string)contentLengthRegex.GetGroupNames().GetValue(2)].Value.Trim();

                Console.WriteLine(headers);
                Console.WriteLine("The content length is: " + contentLength);
            }
        }

        private static byte[] GetRequest(string url)
        {
            Console.WriteLine("GET " + url + " HTTP/1.0\r\n\r\n");
            return Encoding.ASCII.GetBytes("GET " + url + " HTTP/1.0\r\n\r\n");
        }

        private static void InitHost()
        {
            host = "www.cs.ubbcluj.ro";
            port = 80;

            InitPaths(); 
        }

        private static void InitPaths()
        {
            paths.Add("/~rlupsa/edu/pdp/lab-5-futures-continuations.html");
            paths.Add("/~rlupsa/edu/pdp/lab-4-complex-sync.html");
            paths.Add("/~rlupsa/edu/pdp/lab-3-async.html");
        }
    }
}
