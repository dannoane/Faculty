import { List, Range } from 'immutable';

class Decrypt {

  constructor(key, encryptedText) {

    this.n = 27;
    this.key = this._validateKey(key);
    this.encryptedText = this._validateEncryptedText(encryptedText);
  }

  _gcd(a, b) {

    while (b !== 0) {
        [a, b] = [b, a % b];
    }

    return a;
  }

  _validateKey(key) {

    if (typeof(key) !== 'object') {
      throw "Invalid key type";
    }

    key = key.map(k => Number.parseInt(k)).filter(k => Number.isInteger(k));

    if (key.length !== 2 || this._gcd(key[0], this.n) !== 1) {
      throw "Invalid key"
    }

    return key;
  }

  _validateEncryptedText(encryptedText) {

    if (typeof(encryptedText) !== 'string' || !/^[ A-Z]+$/.test(encryptedText)) {
      throw "Encrypted text must be an uppercase english letters only text";
    }

    return encryptedText;
  }

  _modInverse(a, b) {

    a %= b;

    for (let x = 1; x < b; x++) {
      if ((a * x) % b === 1) {
        return x;
      }
    }
  }

  decrypt() {

    let alphabet = Range('A'.charCodeAt(0))
      .map(cc => String.fromCharCode(cc))
      .takeWhile(c => c <= 'Z')
      .toList()
      .unshift(' ');

    let encryptedText = List(this.encryptedText.split(''));

    return encryptedText
      .map(c => alphabet.indexOf(c))
      .map(y => (this._modInverse(this.key[0], this.n) * (y - this.key[1])) % this.n)
      .map(cc => alphabet.get(cc).toLowerCase())
      .join('');
  }
}

export default Decrypt;
