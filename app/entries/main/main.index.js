import React from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter, Route, Switch } from 'react-router-dom';
import LoadingPage from '../../pages/Loading';
import ErrorBoundary from '../../components/ErrorBoundary/ErrorBoundary';
import * as stateKeeper from '../../common/stateKeeper';
import * as api from '../../common/api';
import "../../common.scss";

const EventsOverviewPage = React.lazy(() => import('../../pages/EventsOverview/EventsOverview'));
const EventPage = React.lazy(() => import('../../pages/Event/Event'));

if(!stateKeeper.isAuthenticated()) {
    window.location = "/";
}

ReactDOM.render(<MainPage/>, document.getElementById('root'));

function MainPage() {
    const [userInfo, setUserInfo] = React.useState(stateKeeper.getUserInfo());

    React.useEffect(() => {
        let newUserInfo = api.getUserInfo()
        .then(newUserInfo => {
            let didUpdate = stateKeeper.maybeUpdateUserInfo(newUserInfo);
            if(didUpdate) {
                // only update state if the user info has changed
                setUserInfo(newUserInfo);
            }
        })
        .catch((err) => {
            console.warn(err);
            stateKeeper.clearLogin();
            window.location = "/";
        })
    }, []);

    return <ErrorBoundary errorComponent={<ErrorMessage/>}>
        <BrowserRouter>
            {/* display loading page while loading components */}
            <React.Suspense fallback={<LoadingPage/>}>
                {/* pass UserContext to all components */}
                <stateKeeper.UserContext.Provider value={userInfo}>
                    {/* display correct component in accordance to the current page */}
                    <Switch>
                        <Route path="/you/event/:eventID">
                            <EventPage/>
                        </Route>
                        <Route path="/you">
                            <EventsOverviewPage/>
                        </Route>
                    </Switch>
                </stateKeeper.UserContext.Provider>
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