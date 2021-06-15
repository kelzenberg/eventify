import React from 'react';
import PropTypes, { func } from 'prop-types';
import Datetime from 'react-datetime';
import "./Datetime.scss";

export default function Timespan(props) {
    if(props.editing === true) {
        return <TimespanEditor {...props} />
    } else {
        return <TimespanViewer {...props} />
    }
}

function TimespanViewer(props) {
    if(props.from === null && props.to === null) {
        return <div className="text-left me-auto">
            <span className="text-primary">No Date set yet</span>
        </div>
    }

    let fromDateObject = new Date(props.from);
    let toDateObject = new Date(props.to);

    // create date range
    // We use toLocaleDateString to create a date that is appropriate for the users location.
    // Then we try to find matching ends in the strings and remove that in the 'from' date.
    // That is supposed to remove the year, maybe month or even day if they are equal.
    let fromDate = fromDateObject.toLocaleDateString();
    let toDate = toDateObject.toLocaleDateString();

    let dateRange = ""
    if(props.from === null) {
        dateRange = `... - ${toDate}`;
    } else if(props.to === null) {
        dateRange = `${fromDate} - ...`;
    } else {
        for(let i = 0; i < fromDate.length; i++) {
            let end = fromDate.slice(i); // get end of the string starting at i
            if(toDate.endsWith(end)) {
                // We found a common ending of both dates.
                // Now we will cut that away from the first date.
                fromDate = fromDate.slice(0, i);
                break;
            }
        }

        if(fromDate.length == 0) {
            dateRange = toDate; // both dates are on the same day
        } else {
            dateRange = `${fromDate} - ${toDate}`;
        }
    }

    // create time range
    let fromTime = fromDateObject.toLocaleTimeString();
    fromTime = fromTime.slice(0, fromTime.lastIndexOf(":")); // cut off seconds
    let toTime = toDateObject.toLocaleTimeString();
    toTime = toTime.slice(0, toTime.lastIndexOf(":")); // cut off seconds
    let timeRange = "";
    if(props.from === null) {
        timeRange = `... - ${toTime}`;
    } else if(props.to === null) {
        timeRange = `${fromTime} - ...`;
    } else {
        timeRange = `${fromTime} - ${toTime}`;
    }

    return <div className="text-left me-auto">
        <span className="text-primary">{dateRange}</span><br/>
        <span className="text-primary fw-light">{timeRange}</span>
    </div>
}

Timespan.propTypes = {
    from: PropTypes.oneOfType([PropTypes.instanceOf(Date), PropTypes.string]),
    to: PropTypes.oneOfType([PropTypes.instanceOf(Date), PropTypes.string]),
    editing: PropTypes.bool,
    onChangeFrom: PropTypes.func,
    onChangeTo: PropTypes.func
}

export function TimespanEditor(props) {
    const commonDateProps = {
        locale: navigator.language
    }

    let fromDate = props.from === null ? null : new Date(props.from);
    let toDate = props.to === null ? null : new Date(props.to);

    return <div className="mb-3">
        <div className="mb-1">
            <label className="form-label">Von</label>
            <Datetime
                {...commonDateProps}
                value={fromDate}
                onChange={momDate => props.onChangeFrom(PrepDatetimeDate(momDate))}
            />
        </div>
        <div>
            <label className="form-check-label">Bis</label>
            <Datetime 
                {...commonDateProps}
                value={toDate}
                onChange={momDate => props.onChangeTo(PrepDatetimeDate(momDate))}
            />
        </div>
    </div>
}

export function PrepDatetimeDate(momentDate) {
    if(momentDate === "") return null;
    return new Date(momentDate);
}