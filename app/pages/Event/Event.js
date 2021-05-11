import React from 'react';
import Header from "../../components/Header/Header";
import Title from "../../components/Title/Title";
import Timespan from "../../components/Timespan/Timespan";
import Module from "../../components/Modules/Modules";

export default function EventPage() {
    let event = {
        id: 0,
        name: "My first Event",
        start: new Date(),
        end: new Date(),
        memberCount: 3,
        description: "We are going to go somewhere where it is really nice and then we are gonna have an exciting time, all while I can organze the event easily!",
        members: [{
            name: "Calliope",
            id: 0,
        }, {
            name: "Elliot",
            id: 1
        }, {
            name: "Heinz-Dieter",
            id: 2
        }],
        modules: [
            {
                type: "checklist",
                name: "Packing list",
                items: [
                    {
                        name: "Toothpaste",
                        description: "The fresh-minty one!",
                        done: false,
                        assignees: [0, 2]
                    }, {
                        name: "Sunscreen",
                        description: "I am burning already...",
                        done: true,
                        assignees: []
                    }, {
                        name: "Music Speaker",
                        description: "Let's listen to Genesis",
                        done: false,
                        assignees: [1]
                    }, {
                        name: "Snorkel Equipment",
                        description: "",
                        done: false,
                        assignees: []
                    }
                ]
            }
        ],
    };

    const [editing, setEditing] = React.useState(false);

    return <>
        <Header/>
        <Title title={event.name} breadcrumbs={["Events", event.name]}>
            <button className="btn btn-outline-primary me-4" hidden={editing}>GO BACK</button>
            <button className="btn btn-outline-secondary" onClick={() => setEditing(true)} hidden={editing}>EDIT</button>
            <button className="btn btn-outline-secondary me-4" onClick={() => setEditing(false)} hidden={!editing}>CANCEL</button>
            <button className="btn btn-primary" onClick={() => setEditing(false)} hidden={!editing}>SAVE</button>
        </Title>
        <div className="container">
            <div className="row gx-5">
                <div className="col col-12 col-md-4 col-lg-3">
                    <div className="row mb-3">
                        <Details event={event}/>
                    </div>
                    <div className="row mb-3">
                        <Members event={event}/>
                    </div>
                </div>
                <div className="col">
                    {event.modules.map((module, i) => <div className="row mb-3" key={i} >
                    <Module event={event} moduleData={module}/>
                    </div>)}
                </div>
            </div>
        </div>
    </>
}

function Details({event}) {
    return <div className="card">
        <div className="card-body">
            <p className="fw-bold">Event Details</p>
            <Timespan from={new Date()} to={new Date()}/>
            <p className="mt-3">{event.description}</p>
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
