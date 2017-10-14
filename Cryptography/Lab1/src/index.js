import React from 'react';
import { render } from 'react-dom';
import { Provider } from 'react-redux';
import App from './Container/App';
import Store from './Lib/Store';

const store = Store();

render(
  <Provider store={store}>
    <App />
  </Provider>,
  document.getElementById('root')
);
