using System;
using System.Collections.Generic;
using System.Net.Sockets;
using System.Text;
using System.Threading;

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
            MethodOne();
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
                    Console.WriteLine(String.Format("Read {0} byte from socket" +
                                       "data = {1} ", strContent.Length, strContent));
                    allDone.Set();
                }
                s.Close();
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
