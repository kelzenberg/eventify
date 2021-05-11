import React from 'react';
import PropTypes from 'prop-types';

export default function ChecklistModule(props) {
    let moduleID = props.moduleData.type + "_"+props.moduleData.name;

    return <>
        <table className="table table-borderless table-hover align-middle">
            <thead>
                <tr>
                <th scope="col" style={{width: "15px"}}></th>
                <th scope="col">List Item</th>
                <th scope="col" className="text-end">Assignees</th>
                <th scope="col" style={{width: "15px"}}></th>
                </tr>
            </thead>
            <tbody>
                {props.moduleData.items.map(item => <tr key={item.name} aria-labelledby={moduleID + "_name"}>
                    <th scope="row">
                        <input className="form-check-input" type="checkbox" aria-label="Item Done"/>
                    </th>
                    <td>
                        <span style={{fontWeight: 500}} id={moduleID + "_name"}>{item.name}</span>
                        <br/>
                        <span>{item.description}</span>
                    </td>
                    <td className="text-end">
                        {item.assignees.map(userID => <div
                            key={userID}
                            role="button"
                            aria-label={"Unassign " + (props.event.members.find(m => m.id == userID).name)}
                            title={"Unassign " + (props.event.members.find(m => m.id == userID).name)}
                            className="iconBox iconBox-removable me-2"
                            style={{backgroundImage: "url(/assets/icons/user-image.svg)"}}
                        >
                            {/* <img src={`/api/users/${userID}/image`} alt=""/> */}
                        </div>)}
                        
                        <div
                            className="iconBox"
                            role="button"
                            title="Assign Attendee"
                            aria-label="Assign Attendee"
                            style={{backgroundImage: "url(/assets/icons/add.svg)"}}
                        />
                    </td>
                    <td>
                        <img className="p-2" role="button" title="Remove Item" aria-label="Remove Item" src="/assets/icons/remove.svg"/>
                    </td>
                </tr>)}
            </tbody>
        </table>
        <div className="rounded border-dashed border-gray w-100 p-3 text-center text-primary" role="button" aria-label="Add new Item" onClick={null}>
            <img src="/assets/icons/add.svg" alt="" className="pe-2" style={{verticalAlign: "sub"}}/>
            ADD NEW ITEM
        </div>
    </>
}

ChecklistModule.propTypes = {
    event: PropTypes.object,
    moduleData: PropTypes.object
}