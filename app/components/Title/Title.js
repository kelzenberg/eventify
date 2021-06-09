import React from 'react';
import PropTypes from 'prop-types';
import { Link } from 'react-router-dom';

export default function Title(props) {
    let breadcrumbs = [];
    
    if(props.breadcrumbs != undefined && props.breadcrumbs.length != 0) {
        breadcrumbs = props.breadcrumbs.map((crumb, i) => {
            if(typeof crumb == "string") return <span key={i}>{crumb}</span>;
            if(typeof crumb == "object") {
                return <Link to={crumb.link} key={i}>{crumb.name}</Link>
            }
            return <span key={i}></span>;
        })
        .reduce((prev, curr) => [prev, " > ", curr]);
    }

    return <div className="container mb-4 mt-5">
        <span className="text-muted">{breadcrumbs}</span>
        <div className="d-flex">
            <h1 className="fw-bolder me-auto">{props.title}</h1>
            <div className="">
                {props.children}
            </div>
        </div>
    </div>
}

Title.propTypes = {
    title: PropTypes.string.isRequired,
    breadcrumbs: PropTypes.arrayOf(
        PropTypes.oneOfType([PropTypes.string, PropTypes.object])
    )
}