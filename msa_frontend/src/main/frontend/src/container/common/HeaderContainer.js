import { faHubspot } from "@fortawesome/free-brands-svg-icons";
import { faSearch } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import "bootstrap/dist/css/bootstrap.css";
import Navbar from "react-bootstrap/Navbar";
import "../../css/HeaderContainer.css";

const HeaderContainer = () => {
  return (
    <div>
      <Navbar bg="light" variant="light">
        <div className="container">
          <div className="title">
            <FontAwesomeIcon icon={faHubspot} size="lg" />
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
            <ul class="navbar-nav">
              <li class="nav-item">
                <div class="nav-link" aria-current="page">
                  로그인
                </div>
              </li>
              <hr />
              <li class="nav-item">
                <div class="nav-link">회원가입</div>
              </li>
            </ul>
          </div>
          <div className="navigation"></div>
        </div>
      </Navbar>
    </div>
  );
};

export default HeaderContainer;
