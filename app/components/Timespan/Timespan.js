import React from 'react';
import PropTypes from 'prop-types';

export default function Timespan(props) {
    // create date range
    // We use toLocaleDateString to create a date that is appropriate for the users location.
    // Then we try to find matching ends in the strings and remove that in the 'from' date.
    // That is supposed to remove the year, maybe month or even day.
    let fromDate = props.from.toLocaleDateString();
    let toDate = props.to.toLocaleDateString();

    for(let i = 0; i < fromDate.length; i++) {
        let end = fromDate.slice(i); // get end of the string starting at i
        if(toDate.endsWith(end)) {
            // We found a common ending of both dates.
            // Now we will cut that away from the first date.
            fromDate = fromDate.slice(0, i);
            break;
        }
    }

    let dateRange = "";
    if(fromDate.length == 0) {
        dateRange = toDate; // both dates are on the same day
    } else {
        dateRange = `${fromDate} - ${toDate}`;
    }

    // create time range
    let fromTime = props.from.toLocaleTimeString();
    fromTime = fromTime.slice(0, fromTime.lastIndexOf(":")); // cut off seconds
    let toTime = props.from.toLocaleTimeString();
    toTime = toTime.slice(0, toTime.lastIndexOf(":")); // cut off seconds
    let timeRange = `${fromTime} - ${toTime}`;

    return <div className="text-left me-auto">
        <span className="text-primary">{dateRange}</span><br/>
        <span className="text-primary fw-light">{timeRange}</span>
    </div>
}

Timespan.propTypes = {
    from: PropTypes.instanceOf(Date),
    to: PropTypes.instanceOf(Date)
}