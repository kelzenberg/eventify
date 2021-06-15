import React from 'react';
import update from 'immutability-helper';
import { Modal } from 'react-bootstrap';
import * as api from "../../common/api";

export function ModuleCreator(props) {
    const [errorMessage, setErrorMessage] = React.useState(null);
    const [moduleType, setModuleType] = React.useState("");
    const [options, setOptions] = React.useState({});

    // NOTE: to store a function in the state we have to supply a function that returns the function that we actually want to store
    const [OptionsEditor, setOptionsEditor] = React.useState(() => Empty);
    const [apiCall, setApiCall] = React.useState(() => () => {});

    function reset() {
        setErrorMessage("");
        setModuleType("");
        setOptions({});
        setOptionsEditor(() => Empty);
        setApiCall(() => () => {});
        props.onHide();
    }

    React.useEffect(() => {
        setOptions({});
        switch(moduleType) {
            case "expenseSharingModules":
                setOptionsEditor(() => ExpenseSharingOptions);
                setApiCall(() => options => {
                    return api.addExpenseSharingModule(props.eventID, options.title, options.description);
                });
            break;
        }
    }, [moduleType, props.eventID]);

    function addModule() {
        if(moduleType === "") return;

        apiCall(options)
        .then(moduleData => {
            props.onModuleAdded(moduleType, moduleData);
            reset();
        })
        .catch(err => {
            console.warn(err);
            setErrorMessage("The Module could not be created.");
        });
    }

    return <Modal show={props.show} onHide={() => props.onHide()}>
        <Modal.Header closeButton>
            <Modal.Title>Add a module</Modal.Title>
        </Modal.Header>
        <Modal.Body>
            <form className="was-validated">
                <div className="mb-3">
                    <label className="form-label" htmlFor="moduleID">Module</label><br/>
                    <select className="form-select" id="moduleID" value={moduleType} onChange={e => setModuleType(e.target.value)} required>
                        <option value="" disabled>...</option>
                        <option value="expenseSharingModules">Expense Sharing</option>
                        <option value="checklistModule" disabled>Checklist</option>
                    </select>
                </div>
                {/* {OptionsEditor === null ? null : */}
                    <OptionsEditor options={options} onOptionsChanged={setOptions}/>
                {/* } */}
            </form>
            <span className="text-warning fw-bold" hidden={errorMessage === null}>{errorMessage}</span>
        </Modal.Body>
        <Modal.Footer>
            <button type="button" className="btn btn-primary" onClick={addModule} disabled={moduleType === ""}>Add Module</button>
        </Modal.Footer>
    </Modal>
}

function Empty() {
    return <></>;
}

function ExpenseSharingOptions({options, onOptionsChanged}) {
    if(options.title == undefined) options.title = "";
    if(options.description == undefined) options.description = "";

    function changeOption(name, value) {
        onOptionsChanged(update(options, {[name]: {$set: value}}));
    }

    return <>
        <div className="mb-3">
            <label className="form-label" htmlFor="moduleCreatorExpenseSharingName">Title</label><br/>
            <input type="text" className="form-control" id="moduleCreatorExpenseSharingName" value={options.title} onChange={e => changeOption("title", e.target.value)} required></input>
        </div>
        <div className="mb-3">
            <label className="form-label" htmlFor="moduleCreatorExpenseSharingDescription">Description</label><br/>
            <textarea id="moduleCreatorExpenseSharingDescription" className="form-control" style={{minHeight: "100px"}} value={options.description} onChange={e => changeOption("description", e.target.value)} required></textarea>
        </div>
    </>;
}