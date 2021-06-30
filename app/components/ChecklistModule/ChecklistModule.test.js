import React from 'react';
import { render } from '@testing-library/react';
import "@testing-library/jest-dom/extend-expect";
import ChecklistModule from './ChecklistModule';

const event = {
    id: "123456789",
    title: "Event Title",
    startedAt: new Date("2021-02-27T23:38:43.000Z"),
    endedAt: new Date("2021-06-27T23:38:43.000Z"),
    amountOfUsers: 5,
    description: "Event Description",
    members: [{
        name: "Calliope",
        id: 0,
    }, {
        name: "Elliot",
        id: 1
    }, {
        name: "Heinz-Dieter",
        id: 2
    }],
};

const checklistModule = {
    type: "checklist",
    name: "Packing list",
    items: [
        {
            name: "Toothpaste",
            description: "The fresh-minty one!",
            done: false,
            assignees: [0, 2]
        }, {
            name: "Sunscreen",
            description: "I am burning already...",
            done: true,
            assignees: []
        }, {
            name: "Music Speaker",
            description: "Let's listen to Genesis",
            done: false,
            assignees: [1]
        }, {
            name: "Snorkel Equipment",
            description: "",
            done: false,
            assignees: []
        }
    ]
};

test('Snapshot', () => {
    let { container } = render(
        <ChecklistModule event={event} moduleData={checklistModule} htmlID="SomeID"/>
    );

    expect(container).toMatchSnapshot();
});