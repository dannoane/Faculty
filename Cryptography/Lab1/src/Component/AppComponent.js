import React from 'react';
import PropTypes from 'prop-types';
import Encrypt from './../Lib/Encrypt';
import Decrypt from './../Lib/Decrypt';

const AppComponent = ({
  chiperKey,
  plainText,
  transformedText,
  mode,
  error,
  onKey,
  onPlainText,
  onTransformedText,
  onMode,
  onError
}) => {

  const _getKey = (chiperKey) => {

    return chiperKey
      .replace(/(?!,)\D+/, '')
      .split(',');
  }

  const _transform = () => {

    onError('');

    try {
      if (mode === 'encrypt') {
        let encrypt = new Encrypt(_getKey(chiperKey), plainText);
        onTransformedText(encrypt.encrypt());
      }
      else {
        let decrypt = new Decrypt(_getKey(chiperKey), plainText);
        onTransformedText(decrypt.decrypt());
      }
    }
    catch (err) {
      onError(err.message || err);
    }
  };

  return (
    <div>
      <form onSubmit={(e) => { e.preventDefault(); _transform() }}>
        <label>
          Key:
          <input type="text" value={chiperKey} onChange={(e) => { onKey(e.target.value); }} />
        </label>
        <label>
          Mode:
          <select value={mode} onChange={(e) => { onMode(e.target.value); }}>
              <option value="encrypt">Encrypt</option>
              <option value="decrypt">Decrypt</option>
            </select>
        </label>
        <br />
        <label>
          Plain text: <br />
          <textarea rows={10} cols={72} value={plainText} onChange={(e) => { onPlainText(e.target.value); }}/>
        </label>
        <br />
        <input type="submit" value="Transform" />
      </form>
      <div>{transformedText}</div>
      <div>{error}</div>
    </div>
  );
}

AppComponent.propTypes = {
  chiperKey: PropTypes.string,
  plainText: PropTypes.string,
  transformedText: PropTypes.string,
  mode: PropTypes.string,
  error: PropTypes.string,
  onKey: PropTypes.func,
  onPlainText: PropTypes.func,
  onTransformedText: PropTypes.func,
  onMode: PropTypes.func,
  onError: PropTypes.func
}

export { AppComponent };
