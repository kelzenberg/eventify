import React from 'react';
import {act, fireEvent, getByRole, render, waitFor} from '@testing-library/react';
import "@testing-library/jest-dom/extend-expect";
import { SmallMembers, FullMembers, __RewireAPI__ as rewireMembers } from './members';
import { UserContext } from '../../common/stateKeeper';
import { ConfirmationDialog, Dialog } from '../../components/Dialog/Dialog';
import * as api from "../../common/api";

const AddMemberDialog =  rewireMembers.__get__("AddMemberDialog");

jest.mock("../../components/Dialog/Dialog");
jest.mock("../../common/api");

const localUser = {
    id: "2",
    displayName: "User 2"
};

const event = {
    id: "123456789",
    users: [{
        id: "1",
        displayName: "User 1",
    }, {
        id: "2",
        displayName: "User 2",
    }, {
        id: "3",
        displayName: "User 3",
    }, {
        id: "4",
        displayName: "User 4",
    }, {
        id: "5",
        displayName: "User 5",
    }, {
        id: "6",
        displayName: "User 6",
    }, {
        id: "7",
        displayName: "User 7",
    }]
};

test('Small Members', async () => {
    let dialogMock = jest.fn(() => null);
    rewireMembers.__Rewire__("AddMemberDialog", dialogMock);
    let eventChangeHandler = jest.fn();
    let setVisibleContentHandler = jest.fn();
    let {container, getByTestId} = render(
        <SmallMembers event={event} onEventChanged={eventChangeHandler} setVisibleContent={setVisibleContentHandler}/>
    );

    expect(container).toMatchSnapshot();

    expect(dialogMock).toHaveBeenCalledWith(expect.objectContaining({
        show: false,
        onHide: expect.any(Function),
        eventID: event.id,
        onEventChanged: eventChangeHandler
    }), expect.anything());

    fireEvent.click(getByTestId("show more button"));
    expect(setVisibleContentHandler).toHaveBeenCalledWith("members");

    fireEvent.click(getByTestId("add member button"));
    expect(dialogMock).toHaveBeenCalledWith(expect.objectContaining({
        show: true
    }), expect.anything());

    act(() => getPropsOfLastCall(dialogMock).onHide());

    expect(dialogMock).toHaveBeenLastCalledWith(expect.objectContaining({
        show: false
    }), expect.anything());

    rewireMembers.__ResetDependency__("AddMemberDialog");
});

describe('Full Members', () => {
    let dialogMock = jest.fn(() => null);

    let eventChangeHandler = jest.fn();
    let setVisibleContentHandler = jest.fn();
    let errorMessageHandler = jest.fn();

    function renderComponent() {
        return render(
            <UserContext.Provider value={localUser}>
                <FullMembers event={event} onEventChanged={eventChangeHandler} setVisibleContent={setVisibleContentHandler} onErrorMessage={errorMessageHandler}/>
            </UserContext.Provider>
        );
    }

    beforeAll(() => {
        rewireMembers.__Rewire__("AddMemberDialog", dialogMock);
    });

    afterAll(() => {
        rewireMembers.__ResetDependency__("AddMemberDialog");
    });

    afterEach(() => {
        jest.clearAllMocks();
    });

    test('Basic', async () => {
        let {container, getByTestId, getAllByTestId} = renderComponent();
        expect(container).toMatchSnapshot();

        expect(dialogMock).toHaveBeenCalledWith(expect.objectContaining({
            show: false,
            onHide: expect.any(Function),
            eventID: event.id,
            onEventChanged: eventChangeHandler
        }), expect.anything());
    
        expect(ConfirmationDialog).toHaveBeenCalledWith(expect.objectContaining({
            show: false,
            onConfirm: expect.any(Function),
            onDeny: expect.any(Function)
        }), expect.anything());
    });

    test('Add Member Dialog', async () => {
        let {container, getByTestId, getAllByTestId} = renderComponent();
        // show add-member-dialog
        fireEvent.click(getByTestId("add member button"));
        expect(dialogMock).toHaveBeenCalledWith(expect.objectContaining({
            show: true
        }), expect.anything());

        // hide dialog again
        act(() => getPropsOfLastCall(dialogMock).onHide());
        expect(dialogMock).toHaveBeenLastCalledWith(expect.objectContaining({
            show: false
        }), expect.anything());
    });

    test('Remove member but abort', () => {
        let {container, getByTestId, getAllByTestId} = renderComponent();
        let removeButton = getAllByTestId("remove member button")[0];
        fireEvent.click(removeButton);
        expect(ConfirmationDialog).toHaveBeenLastCalledWith(expect.objectContaining({
            show: true
        }), expect.anything());
    
        let dialogProps = getPropsOfLastCall(ConfirmationDialog);
        act(() => dialogProps.onDeny());
        expect(ConfirmationDialog).toHaveBeenLastCalledWith(expect.objectContaining({
            show: false
        }), expect.anything());
    });

    test('Remove member but api fails', async () => {
        let {container, getByTestId, getAllByTestId} = renderComponent();
        let removeButton = getAllByTestId("remove member button")[0];
        let warnSpy = jest.spyOn(console, "warn").mockImplementation(() => {});
        fireEvent.click(removeButton);
        api.bounceFromEvent.mockRejectedValueOnce(new Error("Mock API Error"));
        let dialogProps = getPropsOfLastCall(ConfirmationDialog);
        act(() => dialogProps.onConfirm()); // confirm removal
        expect(api.bounceFromEvent).toHaveBeenLastCalledWith(event.id, event.users[0].id); // check api call

        // dialog should be hidden again
        await waitFor(() => 
            expect(ConfirmationDialog).toHaveBeenLastCalledWith(expect.objectContaining({
                show: false
            }), expect.anything())
        );
        // error should be returned
        expect(errorMessageHandler).toHaveBeenCalled();
        warnSpy.mockRestore();
    });
    
    test('Remove member and succeed', async () => {
        let {container, getByTestId, getAllByTestId} = renderComponent();
        let removeButton = getAllByTestId("remove member button")[0];
        fireEvent.click(removeButton);
        api.bounceFromEvent.mockResolvedValueOnce(null);
        let dialogProps = getPropsOfLastCall(ConfirmationDialog);
        act(() => dialogProps.onConfirm()); // confirm removal
        expect(api.bounceFromEvent).toHaveBeenLastCalledWith(event.id, event.users[0].id); // check api call

        // dialog should be hidden again
        await waitFor(() => 
            expect(ConfirmationDialog).toHaveBeenLastCalledWith(expect.objectContaining({
                show: false
            }), expect.anything())
        );

        expect(errorMessageHandler).not.toHaveBeenCalled();
        expect(eventChangeHandler).toHaveBeenCalled();

        expect(container).toMatchSnapshot();
    });
    
    test('Close', () => {
        let {container, getByTestId, getAllByTestId} = renderComponent();
        fireEvent.click(getByTestId("close full members button"));
        expect(setVisibleContentHandler).toHaveBeenLastCalledWith("modules");
    });
});

describe('Add Member Dialog', () => {
    let hideHandler = jest.fn();
    let eventChangeHandler = jest.fn();

    function renderComponent() {
        return render(
            <AddMemberDialog onHide={hideHandler} onEventChanged={eventChangeHandler} eventID={event.id} show={true}/>
        );
    }

    afterEach(() => {
        jest.clearAllMocks();
    });

    test('Visibility', () => {
        let {rerender, container} = render(
            <AddMemberDialog onHide={hideHandler} onEventChanged={eventChangeHandler} eventID={event.id} show={false}/>
        );

        expect(Dialog).toHaveBeenCalledWith(expect.objectContaining({
            show: false
        }), expect.anything());

        rerender(
            <AddMemberDialog onHide={hideHandler} onEventChanged={eventChangeHandler} eventID={event.id} show={true}/>
        );

        expect(Dialog).toHaveBeenCalledWith(expect.objectContaining({
            show: true
        }), expect.anything());

        expect(container).toMatchSnapshot();
    });

    function prepareWithEmail(email) {
        let {getByTestId} = renderComponent();

        fireEvent.change(getByTestId("email input"), {target: {value: email}});
        let dialogProps = getPropsOfLastCall(Dialog);
        expect(dialogProps).toEqual(expect.objectContaining({
            show: true,
            onHide: expect.any(Function),
            title: expect.any(String),
            closable: true,
            buttons: expect.any(Array)
        }));
        expect(dialogProps.buttons).toHaveLength(1);

        return {addButton: dialogProps.buttons[0].onClick, getByTestId};
    }

    test('Empty email', () => {
        let {addButton} = prepareWithEmail("");
        act(() => addButton());
        expect(api.inviteToEvent).not.toHaveBeenCalled();
    });

    test('API Error', async () => {
        let {addButton, getByTestId} = prepareWithEmail("one@two.three");
        api.inviteToEvent.mockRejectedValueOnce(new Error("Mocked API Error"));
        let warnSpy = jest.spyOn(console, "warn").mockImplementation(() => {});
        act(() => addButton());
        await waitFor(() => expect(getByTestId("error message")).toBeVisible());
        expect(api.inviteToEvent).toHaveBeenCalledWith(event.id, "one@two.three");
        
        expect(eventChangeHandler).not.toHaveBeenCalled();
        warnSpy.mockRestore();
    });

    test('Add New Member', async () => {
        let {addButton, getByTestId} = prepareWithEmail("one@two.three");
        api.inviteToEvent.mockResolvedValueOnce("fake new event data");
        act(() => addButton());
        await waitFor(() => 
            expect(api.inviteToEvent).toHaveBeenCalledWith(event.id, "one@two.three")
        );
        expect(getByTestId("error message")).not.toBeVisible();
        expect(eventChangeHandler).toHaveBeenCalledWith("fake new event data");
    });
});

function getPropsOfLastCall(mockFn){
    return mockFn.mock.calls[mockFn.mock.calls.length-1][0];
}