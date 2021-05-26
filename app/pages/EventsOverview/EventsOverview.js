import React from 'react';
import { Link } from 'react-router-dom';
import Header from "../../components/Header/Header";
import Timespan from '../../components/Timespan/Timespan';
import Title from "../../components/Title/Title";
import * as api from '../../common/api';
import "./EventsOverview";

export default function EventsOverviewPage() {
    const [events, setEvents] = React.useState(null);

    React.useEffect(() => {
        api.getAllUserEvents()
        .then(events => setEvents(events))
        .catch(err => {
            console.warn(err);
            // TODO: show error to user
        })
    }, []);

    return <>
        <Header/>
        <Title title="Your Events"/>
        <div className="container">
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
                    <NewEvent />
                </div>
            </div>
        </div>
    </>
}

function Event({event}) {
    return <div className="card h-100">
        <div className="card-body p-4">
            <h5 className="card-title fw-bolder fs-4 mb-4">{event.name}</h5>
            <h6 className="card-subtitle mb-2 text-muted">
                <div className="d-flex">
                    <Timespan from={event.start} to={event.end}/>
                    <div className="text-end pt-2">
                        <span>3</span>
                        <img src="/assets/icons/members.svg" alt="attendees" className="ms-1"/>
                    </div>
                </div>
            </h6>
            <p className="card-text">{event.description}</p>
        </div>
        <div className="card-footer">
            <Link to="/you/event/0">
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

function NewEvent() {
    return <div className="card-outline h-100" aria-label="Create New Event">
        <div className="card-body p-4 h-100 d-flex justify-content-center align-items-center flex-direction-column">
            <div className="text-center">
                <span className="iconBox" style={{backgroundImage: "url(/assets/icons/add.svg)"}}></span>
                <p className="mt-3 text-primary fw-bold">CREATE NEW EVENT</p>
            </div>
        </div>
    </div>
}