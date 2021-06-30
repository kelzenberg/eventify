import React from 'react';
import {fireEvent, render, act, waitFor} from '@testing-library/react';
import "@testing-library/jest-dom/extend-expect";
import { Router, MemoryRouter } from "react-router-dom";
import { createMemoryHistory } from "history";
import EventsOverviewPage from "./EventsOverview";
import BootstrapModalMock from "../../__mocks__/bootstrapModalMock";
import * as api from "../../common/api";

const Event = EventsOverviewPage.__RewireAPI__.__get__("Event");
const NewEvent = EventsOverviewPage.__RewireAPI__.__get__("NewEvent");

EventsOverviewPage.__Rewire__("Modal", BootstrapModalMock);
jest.mock("../../common/api");
jest.mock("../../components/Header/Header");
jest.mock("../../components/Title/Title");
jest.mock("../../components/Timespan/Timespan");

const event = {
    id: "123456789",
    title: "Event Title",
    startedAt: new Date("2021-06-27T23:38:43.000Z"),
    endedAt: new Date("2021-06-27T23:38:43.000Z"),
    amountOfUsers: 5,
    description: "Event Description"
};

afterEach(() => {
    jest.clearAllMocks();
})

describe('Event Card', () => {
    test('Snapshot', () => {
        let {container} = render(
            <MemoryRouter>
                <Event event={event}/>
            </MemoryRouter>
        );

        expect(container).toMatchSnapshot();
    });

    test('Click', () => {
        const history = createMemoryHistory();
        const {getByText} = render(
            <Router history={history}>
                <Event event={event}/>
            </Router>
        );

        fireEvent.click(getByText(event.title));
        expect(history.location.pathname).toBe(`/you/event/${event.id}`);
    });
})

describe('NewEvent Card', () => {
    let refreshEvents = jest.fn();

    afterEach(() => {
        refreshEvents.mockReset();
        api.createEvent.mockClear();
    });

    test('Snapshot', () => {
        let {container} = render(
            <NewEvent refreshEvents={refreshEvents}/>
        );

        expect(container).toMatchSnapshot();
    });

    test('Dialog', () => {
        const {container, getByLabelText, getByText} = render(
            <NewEvent refreshEvents={refreshEvents}/>
        );
    
        // expect the dialog to be hidden
        expect(BootstrapModalMock).toHaveBeenCalledWith(expect.objectContaining({
            show: false,
            onHide: expect.any(Function)
        }), expect.anything());

        // click the card
        fireEvent.click(getByText("new event", {exact: false}));

        // expect the dialog to be visible now
        expect(BootstrapModalMock).toHaveBeenCalledWith(expect.objectContaining({
            show: true,
            onHide: expect.any(Function)
        }), expect.anything());

        // Test interaction
        fireEvent.change(getByLabelText("description", {exact: false}), {target: {value: event.description}});
        fireEvent.change(getByLabelText("start",       {exact: false}), {target: {value: event.startedAt  }});

        fireEvent.click(getByText("Create")); // click the create button
        // because the name is not set nothing should've happened.
        expect(api.createEvent).not.toHaveBeenCalled();

        // now set the name
        fireEvent.change(getByLabelText("name",        {exact: false}), {target: {value: event.title      }});

        // because the button starts a promise we need to also wait for this promise to finish
        // before we can check that all changes have occurred in the component.
        // This waiting has to be wrapped in a call to act() to tell react that we are expecting changes in the background.
        let apiPromise = Promise.resolve();
        api.createEvent.mockReturnValueOnce(apiPromise);

        fireEvent.click(getByText("Create")); // click the create button

        return act(() => {
            return apiPromise.then(() => {
                expect(api.createEvent).toHaveBeenCalledWith(event.title, event.description, event.startedAt);
                expect(refreshEvents).toHaveBeenCalled();
                return Promise.resolve();
            });
        });
    });

    test('API Error', async () => {
        let warnSpy = jest.spyOn(console, "warn").mockImplementation(() => {});

        const {container, getByLabelText, getByText, findByText} = render(
            <NewEvent refreshEvents={refreshEvents}/>
        );

        // click the card
        fireEvent.click(getByText("new event", {exact: false}));

        fireEvent.change(getByLabelText("name",        {exact: false}), {target: {value: event.title      }});
        fireEvent.change(getByLabelText("description", {exact: false}), {target: {value: event.description}});
        fireEvent.change(getByLabelText("start",       {exact: false}), {target: {value: event.startedAt  }});
        
        api.createEvent.mockRejectedValueOnce(new Error("Mocked API Error"));

        fireEvent.click(getByText("Create")); // click the create button

        let error = await findByText("unexpected problem", {exact: false});
        expect(error).toBeVisible();

        warnSpy.mockRestore();
    });
});

describe('Events Overview Page', () => {
    let EventMock;
    let NewEventMock;

    beforeAll(() => { // run once before any test is executed
        // mock Event and NewEvent because we don't want to test their behaviour in here
        EventMock = jest.fn(() => <span>Mocked Event</span>);
        NewEventMock = jest.fn(() => <span>Mocked NewEvent</span>);
        EventsOverviewPage.__Rewire__("Event", EventMock);
        EventsOverviewPage.__Rewire__("NewEvent", NewEventMock);
    });

    afterAll(() => { // run once after all tests where executed
        EventsOverviewPage.__ResetDependency__("Event");
        EventsOverviewPage.__ResetDependency__("NewEvent");
    });

    afterEach(() => { // run after each test
        EventMock.mockClear();
        NewEventMock.mockClear();
        api.getAllUserEvents.mockReset();
    });

    test("Loading", async () => {
        api.getAllUserEvents.mockRejectedValueOnce(new Error("mocked api error"));
        let warnSpy = jest.spyOn(console, "warn").mockImplementation(() => {});
        const {container, getByLabelText, getByText} = render(
            <EventsOverviewPage/>
        );
        await waitFor(() => expect(api.getAllUserEvents).toHaveBeenCalled());
        expect(EventMock).not.toHaveBeenCalled();
        expect(NewEventMock).toHaveBeenCalled();
        expect(container).toMatchSnapshot();
        warnSpy.mockRestore();
    });

    test("Normal", async () => {
        api.getAllUserEvents.mockResolvedValueOnce([event]);
        const {container, getByLabelText, getByText} = render(
            <EventsOverviewPage/>
        );
        await waitFor(() => expect(api.getAllUserEvents).toHaveBeenCalled());
        expect(EventMock).toHaveBeenCalled();
        expect(NewEventMock).toHaveBeenCalled();
        expect(container).toMatchSnapshot();
    });
});