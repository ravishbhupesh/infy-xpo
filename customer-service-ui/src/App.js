import logo from './logo.svg';
import './App.css';
import Chat from './Chat';
import Header from './Header';
import Footer from './Footer';

function App() {
  return (
    <div className="App">
      <Header />
      <div className="app-container">
        <div className="chat-container">
          <Chat />
        </div>
      </div>
      <Footer />
      <Footer />
    </div>
  );
}

export default App;
