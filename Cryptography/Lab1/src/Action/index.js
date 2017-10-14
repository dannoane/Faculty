
export const setKey = (key) => ({
  type: 'SET_KEY',
  value: key
});

export const setPlainText = (plainText) => ({
  type: 'SET_PLAIN_TEXT',
  value: plainText
});

export const setTransformedText = (transformedText) => ({
  type: 'SET_TRANSFORMED_TEXT',
  value: transformedText
});

export const setMode = (mode) => ({
  type: 'SET_MODE',
  value: mode
});

export const setError = (error) => ({
  type: 'SET_ERROR',
  value: error
});
