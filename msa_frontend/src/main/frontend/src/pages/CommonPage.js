import React from "react";
import "../css/CommonPage.css";
import logo from "../Image/HNC.png";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faBars } from "@fortawesome/free-solid-svg-icons";

const CommonPage = () => {
  return (
    <div className="template">
      <div className="sidebar">
        <img src={logo} alt="Logo" className="logo" />
      </div>
      <div className="header">
        <nav className="navbar bg-body-tertiary">
          <div className="container-fluid">
            <button
              className="navbar-toggler"
              type="button"
              data-bs-toggle="collapse"
              data-bs-target="#navbarText"
              aria-controls="navbarText"
              aria-expanded="false"
              aria-label="Toggle navigation"
            >
              <FontAwesomeIcon icon={faBars} />
            </button>
            <a className="navbar-brand" href="#">
              Bootstrap
            </a>
          </div>
        </nav>
      </div>
      <div className="main"></div>
    </div>
  );
};

export default CommonPage;
