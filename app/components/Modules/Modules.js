import React from 'react';
import PropTypes from 'prop-types';
import ChecklistModule from "./ChecklistModule";

export default function Module(props) {
    let moduleContent = null;
    let moduleIcon = null;
    switch(props.moduleData.type) {
        case "checklist":
            moduleContent = <ChecklistModule {...props}/>
            moduleIcon = "checklist.svg";
            break;
        default:
            moduleContent = <span className="text-warning">This Module can't be displayed.</span>
    }

    const [collapsed, setCollapsed] = React.useState(false);
    let contentID = props.moduleData.type + "_"+props.moduleData.name;

    return <div className="card">
        <div className="card-body">
            <div
                className="fw-bold"
                aria-expanded={collapsed}
                aria-controls={"#"+contentID}
                onClick={e => setCollapsed(!collapsed)}
                role="button"
            >
                <img
                    src="/assets/icons/arrow-down.svg"
                    className="p-2"
                    alt=""
                    aria-label="expand or collapse module"
                />
                <img src={"/assets/icons/"+moduleIcon} className="p-2" alt=""/>
                {props.moduleData.name}
            </div>
            <div hidden={collapsed} id={contentID+"collapse"}>
                {moduleContent}
            </div>
        </div>
    </div>
}

Module.propTypes = {
    event: PropTypes.object,
    moduleData: PropTypes.object
}