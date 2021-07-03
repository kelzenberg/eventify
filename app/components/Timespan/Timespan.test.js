import React from 'react';
import {fireEvent, render} from '@testing-library/react';
import "@testing-library/jest-dom/extend-expect";
import Timespan, {prepDatetimeDate, TimespanEditor} from './Timespan';
import moment from 'moment';

beforeAll(() => {
    Object.defineProperty(global.navigator, 'language', {value: 'de-de', configurable: true});
    Date.now = () => (new Date("2021-06-30T01:00:26.000Z")).getTime();
})

test('Prep Datetime Date', () => {
    let d = new Date("2021-06-30T01:00:26.000Z");
    let newDate = prepDatetimeDate(moment(d));
    expect(newDate).toEqual(d);

    expect(prepDatetimeDate("")).toBe(null);
});

test('Timespan Editor', () => {
    const from = new Date("2020-02-11T06:21:59.000Z");
    const to = new Date("2021-06-30T01:00:26.000Z");
    let changeFrom = jest.fn();
    let changeTo = jest.fn();
    let { container, getByLabelText } = render(
        <TimespanEditor from={from} to={to} onChangeFrom={changeFrom} onChangeTo={changeTo} htmlID={"test"}/>
    );

    expect(container).toMatchSnapshot();
    let newFrom = new Date("2010-02-11T06:21:59.000Z");
    let newTo = new Date("2010-06-30T01:00:26.000Z");
    fireEvent.change(getByLabelText(/from/i), {target: {value: newFrom.toISOString()}});
    expect(changeFrom).toHaveBeenCalledWith(newFrom);
    fireEvent.change(getByLabelText(/to/i), {target: {value: newTo.toISOString()}});
    expect(changeTo).toHaveBeenCalledWith(newTo);
    expect(container).toMatchSnapshot();
});

test('Timespan', () => {
    const from = new Date("2020-02-11T06:21:59.000Z");
    const to = new Date("2021-06-30T01:00:26.000Z");
    let { container, rerender } = render(
        <Timespan from={null} to={null} editing={false} onChangeFrom={null} onChangeTo={null} htmlID={"test"}/>
    );
    expect(container).toMatchSnapshot();

    rerender(<Timespan from={from} to={null} editing={false} onChangeFrom={null} onChangeTo={null} htmlID={"test"}/>)
    expect(container).toMatchSnapshot();

    rerender(<Timespan from={null} to={to} editing={false} onChangeFrom={null} onChangeTo={null} htmlID={"test"}/>)
    expect(container).toMatchSnapshot();

    rerender(<Timespan from={from} to={to} editing={false} onChangeFrom={null} onChangeTo={null} htmlID={"test"}/>)
    expect(container).toMatchSnapshot();

    // same date
    rerender(<Timespan from={from} to={from} editing={false} onChangeFrom={null} onChangeTo={null} htmlID={"test"}/>)
    expect(container).toMatchSnapshot();

    // editing
    rerender(<Timespan from={null} to={null} editing={true} onChangeFrom={null} onChangeTo={null} htmlID={"test"}/>)
    expect(container).toMatchSnapshot();
});