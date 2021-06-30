import React from 'react';
import PropTypes, { func } from 'prop-types';
import Datetime from 'react-datetime';
const actualTimespan = jest.requireActual("../Timespan");

const Timespan = jest.fn(props => {
    if(props.editing === true) {
        return <span>Mock Timespan with Editor: <TimespanEditor {...props} /></span>;
    } else {
        return <span>Mock Timespan from {props.from.toISOString()} to {props.from.toISOString()}</span>
    }
});

export default Timespan;

export const TimespanEditor = jest.fn((props) => {
    return <span>Mock TimespanEditor from {props.from.toISOString()} to {props.from.toISOString()}</span>
});

export const prepDatetimeDate = jest.fn(actualTimespan.prepDatetimeDate);