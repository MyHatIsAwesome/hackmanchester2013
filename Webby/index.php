<?php
  $test = "PHP WOO!";
?>
<!DOCTYPE html>
<html>
  <head>
    <title>Artificial Friend - <?php echo $test; ?></title>

    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link href="css/bootstrap.min.css" rel="stylesheet" media="screen">
    <link rel="stylesheet" href="css/style.css"/>

    <script src="js/jquery.2.0.3.min.js"></script>
    <script src="js/bootstrap.min.js"></script>
    <script type="text/javascript" src="js/main.js"></script>

  </head>

  <body>
    <header>
      <h1>Artificial Friend</h1>
    </header>

    <nav>
      <ul class="nav nav-pills">
        <li class="active">
          <a href=".">Home</a>
        </li>
        <li>
          <a href=".">Lorem Ipsum</a>
        </li>
        <li>
          <a href=".">Dolor Amet</a>
        </li>
        <li>
          <a href=".">Sit Amet</a>
        </li>
      </ul>
    </nav>

    <section class="hero-unit">
      <p>They're programmed to talk to you!</p>

      <form class="form-horizontal" action=".">
        <div class="control-group">
          <label class="control-label" for="inputPhone">Phone</label>
          <div class="controls">
            <input type="text" id="inputPhone" placeholder="Phone">
          </div>
        </div>
        <div class="control-group">
          <div class="controls">
            <button type="submit" class="btn">Sign in</button>
          </div>
        </div>
      </form>

      <pre id="code"></pre>

    </section>

    <footer class="text-center">
      <small>
        This is a <a href="http://hackmanchester.com" target="_blank">Hack Manchester</a> 2013 hack using <a href="http://clockworksms.com" target="_blank">Clockwork SMS</a>'s messaging API!<br/>
        This is just a joke ;)<br/>
        &copy; Somebody 2013
      </small>
    </footer>
  </body>
</html>