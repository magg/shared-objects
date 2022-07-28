import logo from './logo.svg';
import './App.css';
import { useEffect, useState } from 'react';
import Calendar from './components/Calendar';

function App() {

  return (
    <div className="App">
      <header className="App-header">
       <h1>Calend3</h1>
       <div id= "slogan"> appointment scheduling</div>
       {<Calendar/>}
      </header>
    </div>
  );
}

export default App;
