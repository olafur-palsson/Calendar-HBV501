import React, { Component } from 'react';
import { BrowserRouter, Route, Switch } from 'react-router-dom'


import './App.css';
import Home from './routes/home';
import Calendar from './routes/calendar';
import Users from './routes/users'

class App extends Component {
  render() {
    return (
      <main className="main">
        <div className="Content">
          <section>
            <BrowserRouter>
              <Switch>
                <Route path="/" exact component={Home} />
                <Route exact path="/Calendar" component={Calendar} />
                <Route exact path="/Users" component={Users} />
                
              </Switch>
            </BrowserRouter>
          </section>
        </div>
      </main>
    );
  }
}

export default App;
