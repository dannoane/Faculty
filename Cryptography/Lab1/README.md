# Problem: Classical cryptography.

Implement Affine cipher.

Create a project with the following features:
* A graphical interface. The user will be given the possibility to choose the characters of the alphabet to be used out of the blank and the 26 letters of the English alphabet. The implicit alphabet will have all the 27 characters.
* Given an encryption key and a plaintext, encrypts the plaintext. There will be a key validation and a plaintext validation.
* Given an encryption key and a ciphertext, computes the decryption key and then decrypts the ciphertext.

### How to run the app:
```sh
npm install
npm start
```

### Affine cipher:

#### Encryption:
* A key of the form __(a, b)__, where __a__ and __b__ are positive integers, is selected by the user. __a__ and __n__ 
must be coprime.
* Each letter from an alphabet with __n__ characters is mapped to an integer __x__ between __0__ and __n - 1__.
* Compute __(a * x + b) % n__ for each letter to obtain the cipher text.

#### Decryption:
* Use the same key as for encryption.
* Each letter of the cipher text is mapped to its coresponding integer.
* Compute __A * (x - b) % n__ for each letter to obtain the plain text. __A__ is the modular multiplicative inverse of __a__
module __n__. __A__ satisfies the ecuation: __(a * A) % n = 1__. 
