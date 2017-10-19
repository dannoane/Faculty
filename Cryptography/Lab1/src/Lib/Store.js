import { createStore } from 'redux';
import { throttle } from 'lodash/throttle';
import { affineCipher } from './../Reducer/AffineCipher';

const loadState = () => {

  return JSON.parse(localStorage.getItem('state')) || undefined;
};

const saveState = ({cipherKey, plainText, mode}) => {

  localStorage.setItem('state', JSON.stringify({cipherKey, plainText, mode}));
}

const Store = () => {

  const persistedState = loadState();
  const store = createStore(affineCipher, persistedState);

  store.subscribe(() => {
    saveState(store.getState());
  });

  return store;
};

export default Store;
