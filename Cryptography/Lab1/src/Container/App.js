import React from 'react'
import { connect } from 'react-redux'
import { AppComponent } from './../Component/AppComponent';
import { setKey, setPlainText, setTransformedText, setMode, setError } from './../Action';

const mapStateToProps = (state) => ({
  chiperKey: state.chiperKey,
  plainText: state.plainText,
  transformedText: state.transformedText,
  mode: state.mode,
  error: state.error
});

const mapDispatchToProps = {
  onKey: setKey,
  onPlainText: setPlainText,
  onTransformedText: setTransformedText,
  onMode: setMode,
  onError: setError
};

const App = connect(
  mapStateToProps,
  mapDispatchToProps
)(AppComponent);

export default App;
