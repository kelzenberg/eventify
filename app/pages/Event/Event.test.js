import React from 'react';
import {fireEvent, render, act, waitFor } from '@testing-library/react';
import "@testing-library/jest-dom/extend-expect";
import { Router, Route } from "react-router-dom";
import { createMemoryHistory } from "history";
import update from 'immutability-helper';
import EventPage from "./Event";
import * as api from "../../common/api";
import Timespan from "../../components/Timespan/Timespan";
import fetcher from '../../common/fetcher';
import Title from '../../components/Title/Title';
import { InfoDialog } from '../../components/Dialog/Dialog';
import { UserContext } from '../../common/stateKeeper';

const ModuleCard = EventPage.__RewireAPI__.__get__("ModuleCard");
const ModuleList = EventPage.__RewireAPI__.__get__("ModuleList");
const Details = EventPage.__RewireAPI__.__get__("Details");

jest.mock("../../common/api");
jest.mock("../../components/Header/Header");
jest.mock("../../components/Title/Title");
jest.mock("../../components/Timespan/Timespan");
jest.mock("../../components/Dialog/Dialog");

const userInfo = {
    id: "localUserID",
    displayName: "Local User",
    email: "one@two.three",
    eventRole: "ORGANISER"
};

const event = {
    id: "123456789",
    title: "Event Title",
    startedAt: new Date("2021-02-27T23:38:43.000Z"),
    endedAt: new Date("2021-06-27T23:38:43.000Z"),
    amountOfUsers: 5,
    description: "Event Description",
    users: [userInfo],
    expenseSharingModules: [
        {
            id: "987654321",
            title: "Expense Sharing Module 1",
            description: "Expense Sharing Description 1",
            payments: []
        }, {
            id: "5ab282ff-3ce8-44b3-bc94-ade7d8f10b30",
            title: "Expense Sharing Module 2",
            description: "Expense Sharing Description 2",
            payments: []
        }
    ]
};

describe('ModuleCard', () => {
    test('Normal', async () => {
        let Child = jest.fn(() => null);
        let { getByText } = render(
            <ModuleCard htmlID="" icon="icon.svg" title="Module Name">
                <Child/>
                <span>Content Stuff</span>
            </ModuleCard>
        );

        expect(Child).toHaveBeenCalled();
        let title = getByText("Module Name");
        expect(title).toBeVisible();

        fireEvent.click(title); // collapse
        await waitFor(() => expect(getByText("Content Stuff")).not.toBeVisible());
    });
});

describe('ModuleList', () => {
    let ModuleCardMock;
    let ModuleCreatorMock;
    let ExpenseSharingModuleMock;
    let ChecklistModuleMock;

    beforeAll(() => { // run once before any test is executed
        // mock Event and NewEvent because we don't want to test their behaviour in here
        ModuleCardMock = jest.fn((props) => <div>Mocked Event Card "{props.title}": {props.children}</div>);
        ModuleCreatorMock = jest.fn((props) => <div>Mocked Module Creator</div>);
        ExpenseSharingModuleMock = jest.fn((props) => <span>Mocked Expense Sharing Module</span>);
        ExpenseSharingModuleMock.icon = "Mocked Expense Sharing Module Icon";
        ChecklistModuleMock = jest.fn((props) => <span>Mocked Checklist Module</span>)
        ChecklistModuleMock.icon = "Mocked Checklist Module Icon";
        EventPage.__Rewire__("ModuleCard", ModuleCardMock);
        EventPage.__Rewire__("ModuleCreator", ModuleCreatorMock);
        EventPage.__Rewire__("ExpenseSharingModule", ExpenseSharingModuleMock);
        EventPage.__Rewire__("ChecklistModule", ChecklistModuleMock);
    });

    afterAll(() => { // run once after all tests where executed
        EventPage.__ResetDependency__("ModuleCard");
        EventPage.__ResetDependency__("ModuleCreatorMock");
        EventPage.__ResetDependency__("ExpenseSharingModule");
        EventPage.__ResetDependency__("ChecklistModule");
    });

    afterEach(() => { // run after each test
        ModuleCardMock.mockClear();
        ModuleCreatorMock.mockClear();
        ExpenseSharingModuleMock.mockClear();
        ChecklistModuleMock.mockClear();
    });


    test('Empty', () => {
        let changeHandler = jest.fn();
        let eventWithoutModules = update(event, {expenseSharingModules: {$set: []}});
        let { container, getByText } = render(
            <ModuleList onEventChanged={changeHandler} event={eventWithoutModules}/>
        );
        expect(getByText("new module", {exact: false})).toBeVisible();
        expect(getByText(/no.*modules/i, {exact: false})).toBeVisible();
        expect(ModuleCardMock).not.toHaveBeenCalled();
        expect(container).toMatchSnapshot();

        expect(ModuleCreatorMock).toHaveBeenCalledWith(expect.objectContaining({
            show: false,
        }), expect.anything());
    });

    test('Module Creator', () => {
        let changeHandler = jest.fn();
        let eventWithoutModules = update(event, {expenseSharingModules: {$set: []}});
        let { getByText } = render(
            <ModuleList onEventChanged={changeHandler} event={eventWithoutModules}/>
        );
        // click button to create new module
        fireEvent.click(getByText("new module", {exact: false}));
        expect(ModuleCreatorMock).toHaveBeenCalledWith(expect.objectContaining({
            show: true,
            onHide: expect.any(Function),
            eventID: event.id,
            onModuleAdded: expect.any(Function)
        }), expect.anything());

        // try callback to add a new module
        let creatorProps = ModuleCreatorMock.mock.calls[0][0];
        creatorProps.onModuleAdded("expenseSharingModules", "New Fake Module");
        expect(changeHandler).toHaveBeenCalled();
        expect(changeHandler.mock.calls[0][0].expenseSharingModules).toContain("New Fake Module");
        
        // hide the creator again
        act(() => {
            creatorProps.onHide();
        });
        expect(ModuleCreatorMock).toHaveBeenCalledWith(expect.objectContaining({
            show: false,
        }), expect.anything());
    });

    test('Expense Sharing Module', () => {
        let changeHandler = jest.fn();
        let { getByText } = render(
            <ModuleList onEventChanged={changeHandler} event={event}/>
        );
        expect(getByText("new module", {exact: false})).toBeVisible();

        // check module card
        expect(ModuleCardMock).toHaveBeenCalledWith(expect.objectContaining({
            htmlID: expect.any(String),
            icon: ExpenseSharingModuleMock.icon,
            title: event.expenseSharingModules[0].title
        }), expect.anything());
        expect(ModuleCardMock).toHaveBeenCalledWith(expect.objectContaining({
            htmlID: expect.any(String),
            icon: ExpenseSharingModuleMock.icon,
            title: event.expenseSharingModules[1].title
        }), expect.anything());
        
        // check module itself
        expect(ExpenseSharingModuleMock).toHaveBeenCalledWith(expect.objectContaining({
            moduleData: event.expenseSharingModules[0],
            event: event,
            htmlID: expect.any(String)
        }), expect.anything());
        expect(ExpenseSharingModuleMock).toHaveBeenCalledWith(expect.objectContaining({
            moduleData: event.expenseSharingModules[1],
            event: event,
            htmlID: expect.any(String)
        }), expect.anything());
    });
});

describe('Details', () => {
    test('Normal', () => {
        let changeHandler = jest.fn();
        let { rerender, getByText, getByLabelText } = render(
            <Details event={event} onChange={changeHandler} editing={false}/>
        );

        expect(changeHandler).not.toHaveBeenCalled();
        
        expect(getByText(event.description, {ignore: "textarea"})).toBeVisible();
        expect(Timespan).toHaveBeenCalledWith(expect.objectContaining({
            from: event.startedAt,
            to: event.endedAt,
            editing: false
        }), expect.anything());

        Timespan.mockClear();

        // Editing
        rerender( <Details event={event} onChange={changeHandler} editing={true}/> )

        expect(Timespan).toHaveBeenCalledWith(expect.objectContaining({
            from: event.startedAt,
            to: event.endedAt,
            editing: true
        }), expect.anything());

        fireEvent.change(getByLabelText("Name"), {target: {value: "New Event Name"}});
        expect(changeHandler).toHaveBeenCalledWith(expect.objectContaining({
            title: "New Event Name"
        }));

        fireEvent.change(getByLabelText("Description"), {target: {value: "New Event Description"}});
        expect(changeHandler).toHaveBeenCalledWith(expect.objectContaining({
            description: "New Event Description"
        }));

        let timespanArgs = Timespan.mock.calls[0][0];

        let newStartDate = new Date("2020-02-03T01:02:03.000Z");
        timespanArgs.onChangeFrom(newStartDate);
        expect(changeHandler).toHaveBeenCalledWith(expect.objectContaining({
            startedAt: newStartDate
        }));

        let newEndDate = new Date("2023-02-03T01:02:03.000Z");
        timespanArgs.onChangeTo(newEndDate);
        expect(changeHandler).toHaveBeenCalledWith(expect.objectContaining({
            endedAt: newEndDate
        }));
    });
});

describe('EventPage', () => {
    let DetailsMock;
    let SmallMembersMock;
    let ModuleListMock;
    let FullMembersMock;

    beforeAll(() => { // run once before any test is executed
        // mock Event and NewEvent because we don't want to test their behaviour in here
        DetailsMock = jest.fn((props) => <div>Mocked Details</div>);
        SmallMembersMock = jest.fn((props) => <div>Mocked Small Members</div>);
        ModuleListMock = jest.fn((props) => <div>Mocked Module List</div>);
        FullMembersMock = jest.fn((props) => <div>Mocked Full Members</div>)
        EventPage.__Rewire__("Details", DetailsMock);
        EventPage.__Rewire__("SmallMembers", SmallMembersMock);
        EventPage.__Rewire__("ModuleList", ModuleListMock);
        EventPage.__Rewire__("FullMembers", FullMembersMock);
    });

    afterAll(() => { // run once after all tests where executed
        EventPage.__ResetDependency__("ModuleCard");
        EventPage.__ResetDependency__("ModuleCreatorMock");
        EventPage.__ResetDependency__("ExpenseSharingModule");
        EventPage.__ResetDependency__("ChecklistModule");
    });

    afterEach(() => { // run after each test
        api.getEvent.mockClear();
        api.saveEvent.mockClear();
        InfoDialog.mockClear();
        DetailsMock.mockClear();
        SmallMembersMock.mockClear();
        ModuleListMock.mockClear();
        FullMembersMock.mockClear();
    });

    test('Unknown Event', async () => {
        api.getEvent.mockRejectedValueOnce(new fetcher.FetchError("Event not Found", fetcher.status.notFound));
        const history = createMemoryHistory();
        history.push('/you/event/' + event.id);

        let { findByText } = render(
            <Router history={history}>
                <Route path="/you/event/:eventID">
                    <UserContext.Provider value={userInfo}>
                        <EventPage/>
                    </UserContext.Provider>
                </Route>
            </Router>
        );

        await findByText(/event.*not.*exist/i);
        expect(api.getEvent).toHaveBeenCalledWith(event.id);
    });

    test('API Error', async () => {
        api.getEvent.mockRejectedValueOnce(new Error("Mocked API Error"));
        const history = createMemoryHistory();
        history.push('/you/event/' + event.id);

        let { findByText } = render(
            <Router history={history}>
                <Route path="/you/event/:eventID">
                    <UserContext.Provider value={userInfo}>
                        <EventPage/>
                    </UserContext.Provider>
                </Route>
            </Router>
        );

        await findByText(/internal error/i);
        expect(api.getEvent).toHaveBeenCalledWith(event.id);
    });

    test('Loading', async () => {
        api.getEvent.mockReturnValueOnce(new Promise(() => {}));
        const history = createMemoryHistory();
        history.push('/you/event/' + event.id);

        let { container } = render(
            <Router history={history}>
                <Route path="/you/event/:eventID">
                    <UserContext.Provider value={userInfo}>
                        <EventPage/>
                    </UserContext.Provider>
                </Route>
            </Router>
        );

        expect(Title).toHaveBeenCalledWith(expect.objectContaining({title: expect.stringContaining("Loading")}), expect.anything())
        await waitFor(() => expect(api.getEvent).toHaveBeenCalledWith(event.id));
    });

    test('Normal', async () => {
        api.getEvent.mockResolvedValueOnce(event);
        const history = createMemoryHistory();
        history.push('/you/event/' + event.id);

        let { getByText } = render(
            <Router history={history}>
                <Route path="/you/event/:eventID">
                    <UserContext.Provider value={userInfo}>
                        <EventPage/>
                    </UserContext.Provider>
                </Route>
            </Router>
        );

        // wait for the event to have loaded
        await waitFor(() => 
            expect(Title).toHaveBeenCalledWith(expect.objectContaining({title: event.title}), expect.anything())
        );

        // snapshot of title content
        expect(Title.mock.calls[Title.mock.calls.length-1][0].children).toMatchSnapshot();

        // Details
        expect(DetailsMock).toHaveBeenCalledWith(expect.objectContaining({
            event: event,
            editing: false,
            onChange: expect.any(Function)
        }), expect.anything());

        // Event Members in Sidebar
        expect(SmallMembersMock).toHaveBeenCalledWith(expect.objectContaining({
            event: event,
            onEventChanged: expect.any(Function),
            setVisibleContent: expect.any(Function)
        }), expect.anything());

        // Module List
        expect(ModuleListMock).toHaveBeenCalledWith(expect.objectContaining({
            event: event,
            onEventChanged: expect.any(Function),
            setVisibleContent: expect.any(Function),
            onErrorMessage: expect.any(Function)
        }), expect.anything());

        // Full Member List
        expect(FullMembersMock).toHaveBeenCalledWith(expect.objectContaining({
            event: event,
            onEventChanged: expect.any(Function),
            setVisibleContent: expect.any(Function),
            onErrorMessage: expect.any(Function)
        }), expect.anything());

        // Editing
        fireEvent.click(getByText(/edit/i));

        let detailProps = getPropsOfLastCall(DetailsMock);
        expect(detailProps.editing).toBeTruthy();
        DetailsMock.mockClear();

        // change the title
        act(() => {
            detailProps.onChange(update(event, {title: {$set: "New and different title for the event"}}));
        })
        expect(DetailsMock).toHaveBeenCalled();
        detailProps = getPropsOfLastCall(DetailsMock);
        expect(detailProps.event.title).toBe("New and different title for the event");
        DetailsMock.mockClear();

        // abort editing and check if the old title is back
        fireEvent.click(getByText(/cancel/i));
        expect(DetailsMock).toHaveBeenCalled();
        detailProps = getPropsOfLastCall(DetailsMock);
        expect(detailProps.editing).toBeFalsy();
        expect(detailProps.event.title).toBe(event.title);

        // enable editing again
        fireEvent.click(getByText(/edit/i));

        detailProps = getPropsOfLastCall(DetailsMock);
        expect(detailProps.editing).toBeTruthy();

        // change the title again
        act(() => {
            detailProps.onChange(update(event, {title: {$set: "New and different title for the event"}}));
        });
        expect(DetailsMock).toHaveBeenCalled();
        detailProps = getPropsOfLastCall(DetailsMock);
        expect(detailProps.event.title).toBe("New and different title for the event");


        // now save
        let rejectAPI;
        api.saveEvent.mockImplementation(() => new Promise((resolve, reject) => rejectAPI = reject));

        fireEvent.click(getByText(/save/i));
        await waitFor(() => expect(getByText("Saving...")).toBeVisible());
        // now abort the pending request
        rejectAPI(new Error("Mocked API Error"));
        await waitFor(() => expect(getByText("Saving...")).not.toBeVisible());
        // check that the error is shown to the user via the dialog
        expect(InfoDialog).toHaveBeenCalledWith(expect.objectContaining({
            show: true,
            title: expect.stringMatching(/not.*saved/i)
        }), expect.anything());
    

        // close the dialog and make sure it disappears
        let dialogProps = getPropsOfLastCall(InfoDialog);
        InfoDialog.mockClear();
        act(() => dialogProps.onHide());
        expect(InfoDialog).toHaveBeenCalledWith(expect.objectContaining({
            show: false,
        }), expect.anything());

        // save but this time the api works
        // but it returns a different title that the one we sent to make sure
        // the version from the api is used.
        api.saveEvent.mockResolvedValueOnce(update(event, {title: {$set: "New Title from the API"}}));
        fireEvent.click(getByText(/save/i));

        await waitFor(() =>
            expect(DetailsMock).toHaveBeenCalledWith(expect.objectContaining({
                event: expect.objectContaining({title: "New Title from the API"})
            }), expect.anything())
        );

        // now we test direct updating of the event state
        act(() => getPropsOfLastCall(ModuleListMock).onEventChanged(update(event, {title: {$set: "Directly updated title"}})));
        await waitFor(() => expect(Title).toHaveBeenCalledWith(expect.objectContaining({
            title: "Directly updated title"
        }), expect.anything()));
    });

    test('Leave', async () => {
        api.getEvent.mockResolvedValueOnce(event);
        const history = createMemoryHistory();
        history.push('/you/event/' + event.id);

        let { getByText } = render(
            <Router history={history}>
                <Route path="/you/event/:eventID">
                    <UserContext.Provider value={userInfo}>
                        <EventPage/>
                    </UserContext.Provider>
                </Route>
            </Router>
        );

        // wait for component to be finished
        await waitFor(() => 
            expect(Title).toHaveBeenCalledWith(expect.objectContaining({title: event.title}), expect.anything())
        );

        // try to leave but an api error occurs
        api.leaveEvent.mockRejectedValueOnce(new Error("Mocked API Error"));
        let warnSpy = jest.spyOn(console, "warn"); // prevent console.warn from being called
        warnSpy.mockImplementation(() => {});
        fireEvent.click(getByText("...", {selector: "button"})); // expand drop down
        fireEvent.click(getByText("Leave"));
        await waitFor(() => expect(api.leaveEvent).toHaveBeenCalled());
        // check if the dialog is shown
        expect(InfoDialog).toHaveBeenCalledWith(expect.objectContaining({
            title: expect.stringMatching(/not.*leave/i)
        }), expect.anything())
        warnSpy.mockRestore();

        // close the dialog and try again, this time succeeding
        act(() => getPropsOfLastCall(InfoDialog).onHide());
        api.leaveEvent.mockResolvedValueOnce(null);
        fireEvent.click(getByText("...", {selector: "button"})); // expand drop down
        // await waitFor(() => expect(getByText("Leave")).toBeVisible());
        fireEvent.click(getByText("Leave"));
        await waitFor(() => expect(api.leaveEvent).toHaveBeenCalled());
        expect(history.location.pathname).toMatch(/.*you$/);
    });
});

function getPropsOfLastCall(mockFn){
    return mockFn.mock.calls[mockFn.mock.calls.length-1][0];
}