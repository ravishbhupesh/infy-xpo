import './App.css';
import Chat from './Chat';
import Header from './Header';
import Footer from './Footer';

function App() {
  return (
    <div className="App">
      <Header />
      <div className="app-container">
        <div className="static-container">
          <h2>Some fancy component</h2>
          <p>Placeholder for more information.</p>
        </div>
        <div className="static-container">
          <h2>Another fancy component</h2>
          <p>Placeholder for more information.</p>
        </div>
        <div className="chat-container">
          <Chat />
        </div>
      </div>
      <Footer />
    </div>
  );
}

export default App;
