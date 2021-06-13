import React from 'react';
import { Link } from 'react-router-dom';
import { useParams } from "react-router-dom";
import { Modal } from 'react-bootstrap';
import update from 'immutability-helper';
import Header from "../../components/Header/Header";
import Title from "../../components/Title/Title";
import Timespan from "../../components/Timespan/Timespan";
import ChecklistModule from "../../components/Modules/ChecklistModule";
import ExpenseSharingModule from "../../components/Modules/ExpenseSharingModule";
import * as api from "../../common/api";
import fetcher from '../../common/fetcher';

export default function EventPage() {
    const [originalEvent, setOriginalEvent] = React.useState(null); // backup data in case that the user aborts editing
    const [event, setEvent] = React.useState(null);
    const [errorMessage, setErrorMessage] = React.useState(null);
    const [editing, setEditing] = React.useState(false);
    const [saving, setSaving] = React.useState(false);
    const [modal, setModal] = React.useState(null);

    let { eventID } = useParams();

    React.useEffect(() => {
        api.getEvent(eventID)
        .then(event => {
            setEvent(event);
            setOriginalEvent(event);
        })
        .catch(err => {
            if(err instanceof fetcher.FetchError && err.code == fetcher.status.notFound) {
                setErrorMessage("This event does not seem to exist.");
            } else {
                setErrorMessage("An internal error occurred that prevented us from showing you the event. Please try again.");
            }
        })
    }, []);

    function handleChange(newEvent) {
        setEvent(newEvent);
    }

    function startEditing() {
        setEditing(true);
    }

    function abortEditing() {
        setEditing(false);
        setEvent(originalEvent);
    }

    function save() {
        setEditing(false);
        setSaving(true);
        api.saveEvent(event)
        .catch(() => {
            setModal({title: "Event could not be saved", message: "Unfortunately a problem prevented us from saving the event. Please try again."})
        })
        .finally(() => {
            setEditing(true);
            setSaving(false);
        })
    }

    if(errorMessage !== null) {
        return <>
            <Header/>
            <Title title="Unknown Event" breadcrumbs={[{name: "Events", link: "/you"}, "Oops"]}></Title>
            <div className="container">
                <div className="row gx-5">
                    <div className="col">
                        <p>{errorMessage}</p>
                    </div>
                </div>
            </div>
        </>
    }

    if(event === null) {
        return <>
            <Header/>
            <Title title="Loading..." breadcrumbs={[{name: "Events", link: "/you"}]}></Title>
            <div className="d-flex justify-content-center">
                <div className="spinner-border" role="status" />
            </div>
        </>
    }

    return <>
        <Header/>
        <Title title={event.title} breadcrumbs={[{name: "Events", link: "/you"}, event.title]}>
            <Link to="/you/events"><button className="btn btn-outline-primary me-4" hidden={editing || saving}>GO BACK</button></Link>
            <button className="btn btn-outline-secondary" onClick={startEditing} hidden={editing || saving}>EDIT</button>
            <button className="btn btn-outline-secondary me-4" onClick={abortEditing} hidden={!editing}>CANCEL</button>
            <button className="btn btn-primary" onClick={save} hidden={!editing}>SAVE</button>
            <button className="btn btn-primary" disabled hidden={!saving}>
                <span className="pe-2">Saving...</span>
                <span className="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
            </button>
        </Title>
        <div className="container">
            <div className="row gx-5">
                <div className="col col-12 col-md-4 col-lg-3">
                    <div className="row mb-3">
                        <div className="col">
                            <Details event={event} editing={editing} onChange={handleChange}/>
                        </div>
                    </div>
                    {/* <div className="row mb-3">
                        <Members event={event}/>
                    </div> */}
                </div>
                <div className="col">
                    <ModuleList event={event}/>
                </div>
            </div>
        </div>

        <Modal show={modal !== null} onHide={() => {setModal(null)}}>
            <Modal.Header closeButton>
                <Modal.Title>{modal === null ? "" : modal.title}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <p>{modal === null ? "" : modal.message}</p>
            </Modal.Body>
            <Modal.Footer>
                <button type="button" className="btn btn-primary" onClick={() => setModal(null)}>OK</button>
            </Modal.Footer>
        </Modal>
    </>
}

function Details({event, onChange, editing}) {
    function handleDescriptionChange(e) {
        onChange(update(event, {description: {$set: e.target.value}}));
    }

    function handleChangeStartDate(e) {
        onChange(update(event, {startedAt: {$set: e}}));
    }

    function handleChangeEndDate(e) {
        onChange(update(event, {endedAt: {$set: e}}));
    }

    function handleChangeName(e) {
        onChange(update(event, {title: {$set: e.target.value}}));
    }

    return <div className="card">
        <div className="card-body">
            <p className="fw-bold">Event Details</p>
            <div hidden={!editing} className="mb-1">
                <label>Name</label>
                <input type="text" className="form-control" value={event.title} onChange={handleChangeName}/>
            </div>
            <Timespan from={event.startedAt} to={event.endedAt} editing={editing} onChangeFrom={handleChangeStartDate} onChangeTo={handleChangeEndDate}/>

            <p className="mt-3" hidden={editing}>{event.description}</p>
            <div hidden={!editing}>
                <label>Description</label>
                <textarea className="form-control" placeholder="Leave a comment here" id="floatingTextarea2" style={{minHeight: "100px"}} value={event.description} onChange={handleDescriptionChange}></textarea>
            </div>
        </div>
    </div>
}

function Members({event}) {
    return <div className="card">
        <div className="card-body">
            <div className="d-flex">
                <p className="fw-bold me-auto">
                    Attendees 
                    <img src="/assets/icons/member-add.svg" className="ps-2"/>
                </p>
                <p className="fw-bold text-muted">
                    {event.members.length}
                    <img src="/assets/icons/members.svg" className="ps-2"/>
                </p>
            </div>
            {event.members.map(m => <div className="my-3" key={m.id}>
                <div className="iconBox iconBox-gray me-2" style={{backgroundImage: "url(/assets/icons/user-image.svg)"}}>
                    {/* <img src={`/api/users/${m.id}/image`}/> */}
                </div>
                <span className="align-middle">{m.name}</span>
            </div>)}
        </div>
    </div>
}

function ModuleList(props) {
    const moduleTypes = {"checklistModule": ChecklistModule, "expenseSharingModules": ExpenseSharingModule};
    let moduleComponents = [];

    for(let moduleType in moduleTypes) {
        let ModuleComponent = moduleTypes[moduleType];
        if(props.event.hasOwnProperty(moduleType) && props.event[moduleType].length != 0) {
            let instances = props.event[moduleType].map((moduleInstance, i) => 
                <ModuleCard
                    key={`${moduleType}_${i}`}
                    htmlID={`${moduleType}_${i}`}
                    icon="checklist.svg"
                    title={moduleInstance.title}
                >
                <ModuleComponent moduleData={moduleInstance} htmlID={`${moduleType}_${i}_module`}/>
            </ModuleCard>)
            moduleComponents = moduleComponents.concat(instances);
        }
    }

    return moduleComponents;
}

function ModuleCard(props) {
    const [collapsed, setCollapsed] = React.useState(true);

    return <div className="card">
        <div className="card-body">
            <div
                className="fw-bold"
                aria-expanded={collapsed}
                aria-controls={"#"+props.htmlID}
                onClick={e => setCollapsed(!collapsed)}
                role="button"
            >
                <img
                    src="/assets/icons/arrow-down.svg"
                    className="p-2"
                    alt=""
                    aria-label="expand or collapse module"
                />
                <img src={"/assets/icons/"+props.icon} className="p-2" alt=""/>
                {props.title}
            </div>
            <div hidden={collapsed} id={props.htmlID+"collapse"}>
                {props.children}
            </div>
        </div>
    </div>
}