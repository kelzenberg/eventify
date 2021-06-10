import React from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter, Route, Switch } from 'react-router-dom';
import LoadingPage from '../../pages/Loading';
import ErrorBoundary from '../../components/ErrorBoundary/ErrorBoundary';
import * as stateKeeper from '../../common/stateKeeper';
import "../../common.scss";

const EventsOverviewPage = React.lazy(() => import('../../pages/EventsOverview/EventsOverview'));
const EventPage = React.lazy(() => import('../../pages/Event/Event'));

if(!stateKeeper.isAuthenticated()) {
    window.location = "/";
}

ReactDOM.render(<MainPage/>, document.getElementById('root'));

function MainPage() {
    return <ErrorBoundary errorComponent={<ErrorMessage/>}>
        <BrowserRouter>
        <React.Suspense fallback={<LoadingPage/>}>
            <Switch>
                <Route path="/you/event/:id">
                    <EventPage/>
                </Route>
                <Route path="/you">
                    <EventsOverviewPage/>
                </Route>
            </Switch>
        </React.Suspense>
    </BrowserRouter>
    </ErrorBoundary>
}

function ErrorMessage() {
    return <div className="container m-5">
        <h1>Oops, something went wrong! :(</h1>
        <p>Unfortunately an internal error occurred. <br/>Please reload the page and try again.</p>
    </div>
}