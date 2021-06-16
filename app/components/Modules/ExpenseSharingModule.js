import React from 'react';
import PropTypes from 'prop-types';
import * as stateKeeper from '../../common/stateKeeper';

export default function ExpenseSharingModule(props) {
    return <>
        <p className="my-2">{props.moduleData.description}</p>
        <table className="table table-borderless table-hover align-middle">
            <tbody>
                {props.moduleData.payments === null ? null : 
                    props.moduleData.payments.map(payment => <Payment payment={payment} key={payment.id}/>)
                }
            </tbody>
        </table>
        <div className="rounded border-dashed border-gray w-100 p-3 text-center text-primary" role="button" aria-label="Add new Item" onClick={null}>
            <img src="/assets/icons/add.svg" alt="" className="pe-2" style={{verticalAlign: "sub"}}/>
            ADD NEW EXPENSE
        </div>
    </>
}

ExpenseSharingModule.icon = "finances.svg";

ExpenseSharingModule.propTypes = {
    event: PropTypes.object,
    moduleData: PropTypes.object
}

function Payment({payment}) {
    let userInfo = React.useContext(stateKeeper.UserContext);

    const months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dez"];
    let date = new Date(payment.createdAt);

    let payer = "You";
    if(payment.payer.id != userInfo) {
        payer = payment.payer.displayName;
    }

    let effectiveShare = -1.23;
    if(effectiveShare > 0) {
        var icon = "↗️";
        var textStyle = "text-primary";
    } else if(effectiveShare < 0) {
        var icon = "↘️";
        var textStyle = "text-secondary";
    } else {
        var icon = "➡️";
        var textStyle = "text-muted";
    }

    return <tr aria-labelledby={payment.id + "_name"}>
        <td className="text-center text-primary fw-slightly-bold">
            <div>{date.getDate()}</div>
            <div>{months[date.getMonth()]}</div>
        </td>
        <td>
            <div className="fw-slightly-bold" id={payment.id + "_name"}>{payment.title}</div>
            <div>{payer} paid {payment.amount} €</div>
        </td>
        <td className="text-end">
            <div>{icon}</div>
            <div className={`fw-slightly-bold ${textStyle}`}>{effectiveShare} €</div>
        </td>
    </tr>
}