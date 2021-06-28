import React from 'react';
import {fireEvent, render} from '@testing-library/react';
import { default as snapshotRenderer } from 'react-test-renderer';
import { UserContext } from "../../common/stateKeeper";
import Header from "../../components/Header/Header";
import { __RewireAPI__ as rewireStateKeeper} from "../../common/stateKeeper";

const userInfo = {
    id: "123456789",
    displayName: "Jane Doe",
    email: "one@two.three"
}

test('Snapshot', () => {
    let component = snapshotRenderer.create(
        <UserContext.Provider value={userInfo}>
            <Header/>
        </UserContext.Provider>
    );

    let tree = component.toJSON();
    expect(tree).toMatchSnapshot();
});

test('logout', () => {
    const {queryByText, getByText} = render(
        <UserContext.Provider value={userInfo}>
            <Header/>
        </UserContext.Provider>
    );

    expect(queryByText(userInfo.displayName, {exact: false})).toBeTruthy();

    let clearLogin = jest.fn();
    rewireStateKeeper.__Rewire__("clearLogin", clearLogin);
    fireEvent.click(getByText("Logout"));
    rewireStateKeeper.__ResetDependency__("clearLogin");
})