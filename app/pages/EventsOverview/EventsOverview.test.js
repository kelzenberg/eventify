import React from 'react';
import { default as snapshotRenderer } from 'react-test-renderer';
import {fireEvent, render, act} from '@testing-library/react';
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

let event = {
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
        let component = snapshotRenderer.create(
            <MemoryRouter>
                <Event event={event}/>
            </MemoryRouter>
        );

        expect(component.toJSON()).toMatchSnapshot();
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
    });

    test('Snapshot', () => {
        let component = snapshotRenderer.create(
            <NewEvent refreshEvents={refreshEvents}/>
        );

        expect(component.toJSON()).toMatchSnapshot();
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
        fireEvent.change(getByLabelText("name",        {exact: false}), {target: {value: event.title      }});
        fireEvent.change(getByLabelText("description", {exact: false}), {target: {value: event.description}});
        fireEvent.change(getByLabelText("start",       {exact: false}), {target: {value: event.startedAt  }});

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
});
