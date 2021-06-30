import React from 'react';
import { Link, useHistory } from 'react-router-dom';
import { Modal } from 'react-bootstrap';
import Datetime from 'react-datetime';
import Header from "../../components/Header/Header";
import Timespan, { prepDatetimeDate } from '../../components/Timespan/Timespan';
import Title from "../../components/Title/Title";
import * as api from '../../common/api';
import "./EventsOverview.scss";

export default function EventsOverviewPage() {
    const [events, setEvents] = React.useState(null);

    React.useEffect(fetchEvents, []);

    function fetchEvents() {
        api.getAllUserEvents()
        .then(events => setEvents(events))
        .catch(err => {
            console.warn(err);
            // TODO: show error to user
        })
    }

    return <>
        <Header/>
        <Title title="Your Events"/>
        <div className="container mb-5">
            <div className="row row-cols-1 row-cols-sm-2 row-cols-md-3 row-cols-lg-4 g-3">
                {/* Loading indicator */}
                <div className="col" hidden={events !== null}>
                    <div className="d-flex justify-content-center">
                        <div className="spinner-border" role="status">
                            <span className="visually-hidden">Loading...</span>
                        </div>
                    </div>
                </div>
                {/* Events */}
                {(events === null ? [] : events).map(e => <div className="col" key={e.id}>
                    <Event event={e}/>
                </div>)}
                {/* New Event */}
                <div className="col">
                    <NewEvent refreshEvents={fetchEvents}/>
                </div>
            </div>
        </div>
    </>
}

function Event({event}) {
    let history = useHistory();

    function openEvent() {
        history.push(`/you/event/${event.id}`);
    }

    return <div className="card h-100">
        <div className="card-body p-4 eventCardBody" onClick={openEvent}>
            <h5 className="card-title fw-bolder fs-4 mb-4">{event.title}</h5>
            <h6 className="card-subtitle mb-2 text-muted">
                <div className="d-flex">
                    <Timespan from={event.startedAt} to={event.endedAt} htmlID={event.id}/>
                    <div className="text-end pt-2">
                        <span>{event.amountOfUsers}</span>
                        <img src="/assets/icons/members.svg" alt="attendees" className="ms-1"/>
                    </div>
                </div>
            </h6>
            <p className="card-text">{event.description}</p>
        </div>
        <div className="card-footer">
            <Link to={`/you/event/${event.id}`}>
                <button className="btn btn-primary me-3" aria-label="Edit Event">
                    Edit
                </button>
            </Link>
            <button className="btn btn-outline-secondary btn-icon" aria-label="Delete Event">
                <img src="/assets/icons/trash.svg"/>
            </button>
        </div>
    </div>
}

function NewEvent(props) {
    const [showDialog, setShowDialog] = React.useState(false);
    const [title, setTitle] = React.useState("");
    const [description, setDescription] = React.useState("");
    const [startDate, setStartDate] = React.useState(tomorrow());
    const [showValidation, setShowValidation] = React.useState(false);
    const [errorMessage, setErrorMessage] = React.useState(null);

    function createNewEvent() {
        if(title == "") {
            setShowValidation(true);
            return;
        }
        
        api.createEvent(title, description, startDate)
        .then(eventData => {
            reset();
            props.refreshEvents();
        })
        .catch(err => {
            console.warn(err);
            setErrorMessage("An unexpected problem prevented us from creating the event. Please try again.");
        });
    }

    function reset() {
        setShowDialog(false);
        setTitle("");
        setDescription("");
        setStartDate(tomorrow());
        setShowValidation(false);
        setErrorMessage(null);
    }

    function tomorrow() {
        let k = new Date();
        k.setDate(k.getDate()+1);
        return k;
    }

    return <>
        <div className="card-outline h-100" aria-label="Create New Event" onClick={() => setShowDialog(true)}>
            <div className="card-body p-4 h-100 d-flex justify-content-center align-items-center flex-direction-column">
                <div className="text-center">
                    <span className="iconBox" style={{backgroundImage: "url(/assets/icons/add.svg)"}}></span>
                    <p className="mt-3 text-primary fw-bold">CREATE NEW EVENT</p>
                </div>
            </div>
        </div>
        <Modal show={showDialog} onHide={reset}>
            <Modal.Header closeButton>
                <Modal.Title>Create a New Event</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <form className={showValidation ? "was-validated" : ""}>
                    <div className="mb-3">
                        <label htmlFor="newEventName" className="form-label">Event Name</label>
                        <input type="text" className="form-control" id="newEventName" required value={title} onChange={e => setTitle(e.target.value)}/>
                    </div>
                    <div className="mb-3">
                        <label htmlFor="newEventDescription" className="form-label">Description</label>
                        <textarea className="form-control" id="newEventDescription" style={{minHeight: "100px"}} value={description} onChange={() => setDescription(event.target.value)}></textarea>
                    </div>
                    <div className="mb-3">
                        <label className="form-label" htmlFor="newEventStart">Start of the Event</label>
                        <Datetime
                            inputProps={{id:"newEventStart"}}
                            locale={navigator.language}
                            value={startDate}
                            onChange={momDate => setStartDate(prepDatetimeDate(momDate))}
                        />
                    </div>
                </form>
                <span className="text-warning fw-bold" hidden={errorMessage === null}>{errorMessage}</span>
            </Modal.Body>
            <Modal.Footer>
                <button type="button" className="btn btn-primary" onClick={createNewEvent}>Create</button>
            </Modal.Footer>
        </Modal>
    </>
}