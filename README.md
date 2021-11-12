<div id="top"></div>
<!--
*** Thanks for checking out the Best-README-Template. If you have a suggestion
*** that would make this better, please fork the repo and create a pull request
*** or simply open an issue with the tag "enhancement".
*** Don't forget to give the project a star!
*** Thanks again! Now go create something AMAZING! :D
-->



<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![GNU General Public License v3.0][license-shield]][license-url]



<!-- PROJECT LOGO -->
<br />
<div align="center">
<h3 align="center">WarHelper</h3>

  <p align="center">
    A discord bot for organizing war rosters in New World
    <br />
    <!-- <a href="https://github.com/github_username/repo_name"><strong>Explore the docs »</strong></a>
    <br />
    <!-- <a href="https://github.com/github_username/repo_name">View Demo</a> -->
    <br />
    ·
    <a href="https://github.com/SteveCookTU/WarHelper/issues">Report Bug</a>
    ·
    <a href="https://github.com/SteveCookTU/WarHelper/issues">Request Feature</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

[![Product Name Screen Shot][product-screenshot]](https://war-helper.com)

[Invite Link](https://discord.com/api/oauth2/authorize?client_id=894254301269086278&permissions=536267455569&scope=bot%20applications.commands)

<p align="right">(<a href="#top">back to top</a>)</p>



### Built With

* [Java](https://www.oracle.com/java/technologies/downloads/)
* [JDA](https://github.com/DV8FromTheWorld/JDA)

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- GETTING STARTED -->
## Getting Started

### Installation

1. Install the latest Java JDK
2. Clone the repo
   ```sh
   git clone https://github.com/SteveCookTU/WarHelper.git
   ```
3. Update the maven dependencies (your IDE should do this automatically)
4. Run maven build

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- USAGE EXAMPLES -->
## Usage

### Slash Commands

· /war - base command

    · alert [server] [faction] [territory] [date] [time] - Creates a war alert in the channel the command was typed in. For synchronization, the parameters must be the same in the multiple servers it's posted in the generate the same unique identifier.
    
    · perm [add_remove] [role] - Owner permission to grant or revoke alert posting access to other roles.
    
· /register - base data register command

    · level [level] - sets the player's level
    
    · mainhand [weapon] [level] - sets the player's mainhand weapon and level
    
    · secondary [weapon] [level] - sets the player's secondary weapon and level
   

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- ROADMAP 
## Roadmap

- [] Feature 1
- [] Feature 2
- [] Feature 3
    - [] Nested Feature

See the [open issues](https://github.com/github_username/repo_name/issues) for a full list of proposed features (and known issues).

<p align="right">(<a href="#top">back to top</a>)</p>-->



<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- LICENSE -->
## License

Distributed under the GNU General Public License v3.0. See `LICENSE.md` for more information.

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- CONTACT -->
## Contact

Project Link: [https://github.com/SteveCookTU/WarHelper](https://github.com/SteveCookTU/WarHelper)

<p align="right">(<a href="#top">back to top</a>)</p>



<!-- ACKNOWLEDGMENTS 
## Acknowledgments

* []()
* []()
* []() 

<p align="right">(<a href="#top">back to top</a>)</p> -->



<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/SteveCookTU/WarHelper.svg?style=for-the-badge
[contributors-url]: https://github.com/SteveCookTU/WarHelper/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/SteveCookTU/WarHelper.svg?style=for-the-badge
[forks-url]: https://github.com/github_username/repo_name/network/members
[stars-shield]: https://img.shields.io/github/stars/SteveCookTU/WarHelper.svg?style=for-the-badge
[stars-url]: https://github.com/SteveCookTU/WarHelper/stargazers
[issues-shield]: https://img.shields.io/github/issues/SteveCookTU/WarHelper.svg?style=for-the-badge
[issues-url]: https://github.com/SteveCookTU/WarHelper/issues
[license-shield]: https://img.shields.io/github/license/SteveCookTU/WarHelper.svg?style=for-the-badge
[license-url]: https://github.com/SteveCookTU/WarHelper/blob/master/LICENSE.md
[product-screenshot]: images/Example.png
