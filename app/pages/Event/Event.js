import React from 'react';
import { Link, useHistory } from 'react-router-dom';
import { useParams } from "react-router-dom";
import { Modal, Dropdown, DropdownButton, ButtonGroup } from 'react-bootstrap';
import update from 'immutability-helper';
import Header from "../../components/Header/Header";
import Title from "../../components/Title/Title";
import Timespan from "../../components/Timespan/Timespan";
import ChecklistModule from "../../components/Modules/ChecklistModule";
import ExpenseSharingModule from "../../components/Modules/ExpenseSharingModule";
import { ModuleCreator } from './ModuleCreator';
import * as api from "../../common/api";
import fetcher from '../../common/fetcher';

export default function EventPage() {
    const [originalEvent, setOriginalEvent] = React.useState(null); // backup data in case that the user aborts editing
    const [event, setEvent] = React.useState(null);
    const [errorMessage, setErrorMessage] = React.useState(null);
    const [editing, setEditing] = React.useState(false);
    const [saving, setSaving] = React.useState(false);
    const [modal, setModal] = React.useState(null);

    let history = useHistory();
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

    function handleEventUpdate(newEvent) {
        setEvent(newEvent);
        setOriginalEvent(newEvent);
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

    function leave() {
        api.leaveEvent(event.id)
        .then(() => {
            history.push(`/you`);
        })
        .catch(err => {
            console.warn(err);
            setModal({title: "Could not leave event", message: "Unfortunately leaving the event was not possible."});
        });
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
            <Link to="/you/events"><button className="btn btn-outline-primary" hidden={editing || saving}>GO BACK</button></Link>
            <button className="btn btn-outline-secondary" onClick={startEditing} hidden={editing || saving}>EDIT</button>
            <button className="btn btn-outline-secondary" onClick={abortEditing} hidden={!editing}>CANCEL</button>
            <button className="btn btn-primary" onClick={save} hidden={!editing}>SAVE</button>
            <button className="btn btn-primary" disabled hidden={!saving}>
                <span className="pe-2">Saving...</span>
                <span className="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
            </button>
            <DropdownButton title="..." as={ButtonGroup} variant="outline-secondary">
                <Dropdown.Item onClick={leave}>Leave</Dropdown.Item>
            </DropdownButton>
        </Title>
        <div className="container">
            <div className="row gx-5">
                <div className="col col-12 col-md-4 col-lg-3">
                    <div className="row mb-3">
                        <div className="col">
                            <Details event={event} editing={editing} onChange={handleChange}/>
                        </div>
                    </div>
                    <div className="row mb-3">
                        <div className="col">
                            <Members event={event} onEventChanged={handleEventUpdate}/>
                        </div>
                    </div>
                </div>
                <div className="col">
                    <ModuleList event={event} onEventChanged={handleEventUpdate}/>
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
                <textarea className="form-control" style={{minHeight: "100px"}} value={event.description} onChange={handleDescriptionChange}></textarea>
            </div>
        </div>
    </div>
}

function Members({event, onEventChanged}) {
    const [showDialog, setShowDialog] = React.useState(false);
    const [emailAddress, setEmailAddress] = React.useState("");
    const [showValidation, setShowValidation] = React.useState(false);
    const [errorMessage, setErrorMessage] = React.useState(null);

    function reset() {
        setShowDialog(false);
        setEmailAddress("");
        setShowValidation(false);
        setErrorMessage(null);
    }

    function addMember() {
        if(emailAddress === "") {
            setShowValidation(true);
            return;
        }

        api.inviteToEvent(event.id, emailAddress)
        .then(eventData => {
            onEventChanged(eventData)
            reset();
        })
        .catch(err => {
            console.warn(err);
            setErrorMessage("An unexpected problem prevented us from inviting the person. Please try again.");
        });
    }

    return <>
        <div className="card">
            <div className="card-body">
                <div className="d-flex">
                    <p className="fw-bold me-auto">
                        Attendees 
                        <img src="/assets/icons/member-add.svg" className="ps-2" onClick={() => setShowDialog(true)} style={{cursor: "pointer"}}/>
                    </p>
                    <p className="fw-bold text-muted">
                        {event.users.length}
                        <img src="/assets/icons/members.svg" className="ps-2"/>
                    </p>
                </div>
                {event.users.map(u => <div className="my-3" key={u.id}>
                    <div className="iconBox iconBox-gray me-2" style={{backgroundImage: "url(/assets/icons/user-image.svg)"}}>
                        {/* <img src={`/api/users/${m.id}/image`}/> */}
                    </div>
                    <span className="align-middle">{u.displayName}</span>
                </div>)}
            </div>
        </div>
        <Modal show={showDialog} onHide={reset}>
            <Modal.Header closeButton>
                <Modal.Title>Invite a new member</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <form className={showValidation ? "was-validated" : ""}>
                    <div className="mb-3">
                        <label htmlFor="newMemberMail" className="form-label">E-Mail</label>
                        <input type="email" className="form-control" id="newMemberMail" required value={emailAddress} onChange={e => setEmailAddress(e.target.value)}/>
                        <div className="form-text">Enter the E-Mail Address of the person you want to invite.</div>
                    </div>
                </form>
                <span className="text-warning fw-bold" hidden={errorMessage === null}>{errorMessage}</span>
            </Modal.Body>
            <Modal.Footer>
                <button type="button" className="btn btn-primary" onClick={addMember}>Invite</button>
            </Modal.Footer>
        </Modal>
    </>
}

function ModuleList(props) {
    const moduleTypes = {"checklistModule": ChecklistModule, "expenseSharingModules": ExpenseSharingModule};
    const [creatorVisible, setCreatorVisible] = React.useState(false);
    let moduleComponents = [];

    function moduleAdded(moduleType, moduleData) {
        props.onEventChanged(update(props.event, {[moduleType]: {$push: [moduleData]}}));
    }

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

    if(moduleComponents.length == 0) {
        moduleComponents.push(<h4 className="text-center" key="no_event">This event does not have any modules yet.</h4>);
    }

    moduleComponents.push(<React.Fragment key="add_module">
        <div className="rounded border-dashed border-gray w-100 p-3 text-center text-primary" role="button" aria-label="Add new Item" onClick={() => {setCreatorVisible(true)}}>
        <img src="/assets/icons/add.svg" alt="" className="pe-2" style={{verticalAlign: "sub"}}/>
            ADD NEW MODULE
        </div>
        <ModuleCreator show={creatorVisible} onHide={() => setCreatorVisible(false)} eventID={props.event.id} onModuleAdded={moduleAdded}/>
    </React.Fragment>);

    return moduleComponents;
}

function ModuleCard(props) {
    const [collapsed, setCollapsed] = React.useState(true);

    return <div className="card mb-3">
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