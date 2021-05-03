import React from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter, Route, Switch } from 'react-router-dom';
import LoadingPage from '../../pages/Loading';

const EventsOverviewPage = React.lazy(() => import('../../pages/EventsOverview/EventsOverview'));

ReactDOM.render(<MainPage/>, document.getElementById('root'));

function MainPage() {
    return <BrowserRouter>
        <React.Suspense fallback={<LoadingPage/>}>
            <Switch>
                <Route path="/you">
                    <EventsOverviewPage/>
                </Route>
            </Switch>
        </React.Suspense>
    </BrowserRouter>
}