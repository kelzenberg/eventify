import React from 'react';
import PropTypes from 'prop-types';

export default function Header(props) {
    let breadcrumbs = props.breadcrumbs == undefined ? [] : props.breadcrumbs;
    return <div className="container mb-4 mt-5">
        <span className="text-muted">{breadcrumbs.join(" > ")}</span>
        <div className="d-flex">
            <h1 className="fw-bolder me-auto">{props.title}</h1>
            <div className="">
                {props.children}
            </div>
        </div>
    </div>
}

Header.propTypes = {
    title: PropTypes.string.isRequired,
    breadcrumbs: PropTypes.arrayOf(PropTypes.string)
}