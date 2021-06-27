const stateKeeper = jest.requireActual("../stateKeeper");

export const UserContext = stateKeeper.UserContext;
export const isAuthenticated = jest.fn();
export const setCredentials = jest.fn();
export const getAuthenticationToken = jest.fn();
export const getUserInfo = jest.fn();
export const maybeUpdateUserInfo = jest.fn();
export const clearLogin = jest.fn();