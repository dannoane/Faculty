import { createStore } from 'redux';
import { throttle } from 'lodash/throttle';
import { affineChiper } from './../Reducer/AffineChiper';

const loadState = () => {

  return JSON.parse(localStorage.getItem('state')) || undefined;
};

const saveState = ({chiperKey, plainText, mode}) => {

  localStorage.setItem('state', JSON.stringify({chiperKey, plainText, mode}));
}

const Store = () => {

  const persistedState = loadState();
  const store = createStore(affineChiper, persistedState);

  store.subscribe(() => {
    saveState(store.getState());
  });

  return store;
};

export default Store;
