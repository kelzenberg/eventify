import React from 'react';
import PropTypes from 'prop-types';
import { ConfirmationDialog, Dialog } from "../../components/Dialog/Dialog";
import { UserContext } from "../../common/stateKeeper";
import * as api from "../../common/api";

export function SmallMembers({event, onEventChanged, setVisibleContent}) {
    const [showDialog, setShowDialog] = React.useState(false);

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
                {event.users.slice(0, 5).map(u => <div className="my-3" key={u.id}>
                    <div className="iconBox iconBox-gray me-2" style={{backgroundImage: "url(/assets/icons/user-image.svg)"}}>
                        {/* <img src={`/api/users/${m.id}/image`}/> */}
                    </div>
                    <span className="align-middle">{u.displayName}</span>
                </div>)}
                <a className="link-primary fw-bold d-block m-2" role="button" onClick={() => setVisibleContent("members")}>More...</a>
            </div>
        </div>
        <AddMemberDialog show={showDialog} onHide={() => setShowDialog(false)} onEventChanged={onEventChanged}/>
    </>
}

SmallMembers.propTypes = {
    event: PropTypes.object.isRequired,
    onEventChanged: PropTypes.func.isRequired,
    setVisibleContent: PropTypes.func.isRequired
}

export function FullMembers(props) {
    const localUserInfo = React.useContext(UserContext);
    const [addUserVisible, setAddUserVisible] = React.useState(false);
    const [bounceUser, setBounceUser] = React.useState(null);

    function removeMember() {
        api.bounceFromEvent(props.event.id, bounceUser.id)
        .then(() => {

        })
        .catch(err => {
            console.warn(err);
            props.onErrorMessage("Unfortunately a problem prevented us from saving the event. Please try again.");
        });
        setBounceUser(null);
    }

    return <>
        <a className="link-primary fw-bold d-block m-2" role="button" onClick={() => props.setVisibleContent("modules")}>&lt; Go back to all modules</a>
        <div className="card mb-3">
            <div className="card-body">
                <div className="d-flex">
                    <p className="fw-bold me-auto fs-5">All {props.event.users.length} Attendees: </p>
                    <button type="button" className="btn btn-outline-primary" onClick={() => setAddUserVisible(true)} aria-label="Invite User">Invite...</button>
                </div>
                <div>
                    {props.event.users.map(u => <div className="my-3 d-flex align-items-center" key={u.id}>
                        <div className="iconBox iconBox-gray me-2" style={{backgroundImage: "url(/assets/icons/user-image.svg)"}}>
                            {/* <img src={`/api/users/${m.id}/image`}/> */}
                        </div>
                        <span className="me-auto">{u.displayName}</span>
                        <button hidden={localUserInfo.id == u.id} type="button" className="btn-close text-end me-2" aria-label="Remove Attendee" onClick={() => setBounceUser(u)}/>
                    </div>)}
                </div>
            </div>
        </div>
        <ConfirmationDialog 
            show={bounceUser !== null}
            title="Remove this participant?"
            message={`Are you sure you want to remove "${bounceUser === null ? "" : bounceUser.displayName}" from the Event?`}
            confirmText="Remove Participant"
            denyText="Cancel"
            onConfirm={removeMember}
            onDeny={() => setBounceUser(null)}
        />
        <AddMemberDialog show={addUserVisible} onHide={() => setAddUserVisible(false)} onEventChanged={props.onEventChanged}/>
    </>
}

FullMembers.propTypes = {
    event: PropTypes.object.isRequired,
    onEventChanged: PropTypes.func.isRequired,
    setVisibleContent: PropTypes.func.isRequired,
    onErrorMessage: PropTypes.func.isRequired
}

function AddMemberDialog(props) {
    const [emailAddress, setEmailAddress] = React.useState("");
    const [showValidation, setShowValidation] = React.useState(false);
    const [errorMessage, setErrorMessage] = React.useState(null);

    function reset() {
        props.onHide();
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
            props.onEventChanged(eventData);
            reset();
        })
        .catch(err => {
            console.warn(err);
            setErrorMessage("An unexpected problem prevented us from inviting the person. Please try again.");
        });
    }

    return <Dialog
        show={props.show}
        onHide={reset}
        title="Invite someone to the event"
        closable={true}
        buttons={[{text: "Invite", onClick:addMember}]}
    >
        <form className={showValidation ? "was-validated" : ""}>
            <div className="mb-3">
                <label htmlFor="newMemberMail" className="form-label">E-Mail</label>
                <input type="email" className="form-control" id="newMemberMail" required value={emailAddress} onChange={e => setEmailAddress(e.target.value)}/>
                <div className="form-text">Enter the E-Mail Address of the person you want to invite.</div>
            </div>
        </form>
        <span className="text-warning fw-bold" hidden={errorMessage === null}>{errorMessage}</span>
    </Dialog>;
}