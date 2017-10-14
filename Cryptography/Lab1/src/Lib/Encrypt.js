import { List, Range } from 'immutable';

class Encrypt {

  constructor(key, plainText) {

    this.n = 27;
    this.key = this._validateKey(key);
    this.plainText = this._validatePlainText(plainText);
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

  _validatePlainText(plainText) {

    if (typeof(plainText) !== 'string' || !/^[ a-z]+$/.test(plainText)) {
      throw "Plain text must be a lowercase english letters only text";
    }

    return plainText;
  }

  encrypt() {

    let alphabet = Range('a'.charCodeAt(0))
      .map(cc => String.fromCharCode(cc))
      .takeWhile(c => c <= 'z')
      .toList()
      .unshift(' ');

    let plainText = List(this.plainText.split(''));

    return plainText
      .map(c => alphabet.indexOf(c))
      .map(x => (this.key[0] * x + this.key[1]) % this.n)
      .map(x => alphabet.get(x).toUpperCase())
      .join('');
  }
}

export default Encrypt;
