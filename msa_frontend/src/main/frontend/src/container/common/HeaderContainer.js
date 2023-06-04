import { faSearch } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import "bootstrap/dist/css/bootstrap.css";
import Navbar from "react-bootstrap/Navbar";
import Logo from "../../Image/honeycomb.png";
import "../../css/HeaderContainer.css";

const HeaderContainer = () => {
  return (
    <div>
      <nav className="navbar navbar-expand-lg">
        <div className="header-container">
          <div className="title">
            {/* <FontAwesomeIcon icon={faHubspot} size="lg" /> */}
            <img className="logo" src={Logo} alt="logo" />
            <Navbar.Brand href="#home">Jobhub</Navbar.Brand>
          </div>
          <div className="search-bar">
            <FontAwesomeIcon icon={faSearch} className="fa-search" />
            <input
              className="search-bar-input"
              type="search"
              aria-label="Search"
              placeholder="원하는 검색어 입력!"
            />
          </div>
          <div className="utility">
            <ul className="navbar-nav">
              <li className="nav-item">
                <div className="nav-link" aria-current="page">
                  로그인
                </div>
              </li>
              <hr />
              <li className="nav-item">
                <div className="nav-link">회원가입</div>
              </li>
            </ul>
          </div>
          <div className="navigation">
            <div className="navbar-brand">Navbar</div>
            <div className="collapse navbar-collapse" id="navbarNavAltMarkup">
              <div className="navbar-nav">
                <div className="nav-link active" aria-current="page">
                  Home
                </div>
                <div className="nav-link">Features</div>
                <div className="nav-link">Pricing</div>
                <div className="nav-link disabled">Disabled</div>
              </div>
            </div>
          </div>
        </div>
      </nav>
    </div>
  );
};

export default HeaderContainer;
