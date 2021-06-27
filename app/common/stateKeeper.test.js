import * as stateKeeper from "./stateKeeper";

beforeEach(() => {
    localStorage.clear();
});

test("authentication token", () => {
    expect(stateKeeper.isAuthenticated()).toBe(false);
    stateKeeper.setCredentials("mock token");
    expect(stateKeeper.isAuthenticated()).toBe(true);
    expect(stateKeeper.getAuthenticationToken()).toBe("mock token");
    stateKeeper.clearLogin();
    expect(stateKeeper.isAuthenticated()).toBe(false);
});

test("user info", () => {
    let warnSpy = jest.spyOn(console, 'warn').mockImplementation(() => {});
    var userInfo = stateKeeper.getUserInfo();
    expect(userInfo.id).toBe("");

    // overwrite because no info is set
    expect(stateKeeper.maybeUpdateUserInfo({id: "maybe id"})).toBe(true);
    userInfo = stateKeeper.getUserInfo();
    expect(userInfo.id).toBe("maybe id");

    // overwrite because the id changed
    expect(stateKeeper.maybeUpdateUserInfo({id: "new maybe id"})).toBe(true);
    userInfo = stateKeeper.getUserInfo();
    expect(userInfo.id).toBe("new maybe id");

    // don't overwrite because info is the same
    expect(stateKeeper.maybeUpdateUserInfo({id: "new maybe id"})).toBe(false);

    stateKeeper.clearLogin();
    userInfo = stateKeeper.getUserInfo();
    expect(userInfo.id).toBe("");
    warnSpy.mockRestore();
})