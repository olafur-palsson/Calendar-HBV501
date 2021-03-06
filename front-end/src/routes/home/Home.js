import React, { Component } from 'react';
import { Link } from 'react-router-dom';

class Home extends Component {
  constructor(props) {
    super(props);
    this.state = { 
      data: null, 
      loading: true, 
      error: false,
    };
    this.onClick = this.onClick.bind(this);
  }

  onClick(e) {
    e.preventDefault();
    console.log('The link was clicked.');
  }

  render() {
    
    return (
        <div className='Buttons'>
          <div className='Button'>
            <Link to='/users'> Users </Link>
          </div>
          <div>
            <Link to='/register'> Register</Link>
          </div>
        </div>
    );
  }
}

export default Home;
