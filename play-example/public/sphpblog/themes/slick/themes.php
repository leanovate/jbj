<?php
  // --------------------------
  // Simple PHP Blog Theme File
  // --------------------------
  //
  // Name: Slick Theme
  // Author: Bill Bateman
  // Version: 0.5.0

  // New features
  // - Dynamic Widths on entry column (NEW)
  // - Comments not shown separate - clicking on the title does it
  // - Number of views show in the date line
  // - Number of comments shown in brackets after the title (NEW)       $entry_array[ 'comment' ][ 'comment_count' ]
  // - Introducing the rating "stars" without totals after them (NEW)     $entry_array[ 'stars_nototals' ]

  theme_init();

  // ---------------
  // Theme Variables
  // ---------------
  function theme_init () {
    global $theme_vars;

    $theme_vars = array();

    // New 0.3.8
    //
    // Optional:
    // "content_width" and "menu_width" area used internally
    // within the theme only. (optional but recommended.)
    $theme_vars[ 'content_width' ] = 640;
    $theme_vars[ 'max_image_width' ] = $theme_vars[ 'content_width' ] - 66;
    $theme_vars[ 'menu_width' ] = 200;

    // New 0.4.6
    //
    // Optional:
    // Disallow Color Settings on Current Theme
    // used if the theme writer is trying to keep with a certain look
    $theme_vars[ 'options'][ 'disallow_colors' ] = 1;

    // Required:
    // "popup_window" "width" and "height" are used to determine
    // the size of window to open for the comment view.
    $theme_vars[ 'popup_window' ][ 'height' ] = 600;
    $theme_vars[ 'popup_window' ][ 'width' ] = 600;
  }

  function theme_blogentry ( $entry_array, $mode='entry' ) {
    global $user_colors, $blog_theme, $blog_config;

    // Default image path.
    $img_path = BASEURL . "themes/" . $blog_theme . "/images/";

    $blog_content = "";
    $blog_content  .= "\n<!-- BLOG ENTRY BEGIN -->\n";

    // New 0.4.4
    // You must have this if you are using the trackback feature.
      if ( $blog_config->getTag('BLOG_TRACKBACK_ENABLED') ) {
      $blog_content  .= "<!--\n";
      $blog_content  .= '<rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"' . "\n";
      $blog_content  .= '         xmlns:dc="http://purl.org/dc/elements/1.1/"' . "\n";
      $blog_content  .= '         xmlns:trackback="http://madskills.com/public/xml/rss/module/trackback/">' . "\n";
      $blog_content  .= '<rdf:Description' . "\n";
      $blog_content  .= '    rdf:about="' . $entry_array[ 'permalink' ][ 'url' ] . '"' . "\n";
      $blog_content  .= '    dc:identifier="' . $entry_array[ 'permalink' ][ 'url' ] . '"' . "\n";
      $blog_content  .= '    dc:title="' . $entry_array[ 'subject' ] . '"' . "\n";
      $blog_content  .= '    trackback:ping="' . $entry_array[ 'trackback' ][ 'ping_url' ] . '" />' . "\n";
      $blog_content  .= '</rdf:RDF>' . "\n";
      $blog_content  .= '-->' . "\n";
     }

    // Display SUBJECT Line
    $blog_content  .= "\n<!-- BLOG TITLE BEGIN -->\n";
    $blog_content  .= '<br /><div class="blog_title">';
    if ( $entry_array[ 'avatarurl' ] != '' ) {
      $blog_content = $blog_content . '<img src="' . $entry_array[ 'avatarurl'] . '" alt="" border="0" align="left" />';  }

    $blog_content  .= '<a href="' . $entry_array[ 'comment' ][ 'url' ] . '">' . $entry_array[ 'subject' ];

    if ( isset( $entry_array[ 'comment' ][ 'comment_count' ] ) ) {
      $blog_content  .= ' (' . $entry_array[ 'comment' ][ 'comment_count' ] . ')';
    }

    if (!empty($entry_array[ 'id' ])) {
        $blog_content  .= '</a><a id="' . $entry_array[ 'id' ] . '">&nbsp;';
    }
    $blog_content  .= '</a>' . "<br />\n";

    $blog_content  .= "</div>\n";
    $blog_content  .= "\n<!-- BLOG TITLE BEGIN -->\n";

    $blog_content  .= "\n<!-- BLOG BODY BEGIN -->\n";
    $blog_content  .= '<div class="blog_body">' . "\n\t";

    // Display DATE
    $blog_content  .= '<div class="blog_date">' . $entry_array[ 'date' ];

    if ( isset( $entry_array[ 'comment' ][ 'count' ] ) ) {
      // Show "( x views )" string...
      $blog_content  .= " ( " . $entry_array[ 'comment' ][ 'count' ] . " )";
    }
    $blog_content  .= "\n\t";

    // Display CATEGORIES
    // This is an array. There can be multiple categories per entry.
    if ( array_key_exists( "categories", $entry_array ) ) {
      $blog_content  .= " - ";
      for ( $i = 0; $i < count( $entry_array[ 'categories' ] ); $i++ ) {
        $blog_content .= '<a href="index.php?category=' . $entry_array[ 'categories_id' ][$i] . '">' . $entry_array[ 'categories' ][$i] . '</a>';
        if ( $i < count( $entry_array[ 'categories' ] ) - 1 ) {
          $blog_content .= ', ';
        }
      }
    }

    if ( isset( $entry_array[ 'createdby' ][ 'text' ] )) {
          $blog_content = $blog_content . '&nbsp;-&nbsp;' . $entry_array[ 'createdby' ][ 'text'];
      }

    $blog_content  .= "</div>\n\t\t";

    // Display BODY TEXT
    $blog_content  .= $entry_array[ 'entry' ];
    $blog_content  .= "\n\t</div>";
    $blog_content  .= "\n<!-- BLOG BODY END -->\n";

    $blog_content  .= "\n<!-- BLOG FOOTER BEGIN -->\n";
    // Display ADD COMMENT, COUNT,TRACKBACK, PERMALINK, and RATING
    $comment_area = "";

    if ( isset( $entry_array[ 'trackback' ][ 'url' ] ) ) {
      // Show 'trackback' string...
      $comment_area  .= '<a href="' . $entry_array[ 'trackback' ][ 'url' ] . '">' . $entry_array[ 'trackback' ][ 'name' ] . '</a>' . "\n";
    }

    if ( $blog_config->getTag('BLOG_ENABLE_PERMALINK')){// New for 0.4.6
      if ( isset( $entry_array[ 'permalink' ][ 'url' ] ) ) {
        // Show 'permalink' string...
        if ( $comment_area != "" ) {
          $comment_area  .= '&nbsp;|&nbsp;';
        }
        $comment_area  .= '<a href="' . $entry_array[ 'permalink' ][ 'url' ] . '">' . $entry_array[ 'permalink' ][ 'name' ] . '</a>' . "\n";
      }
    }

    if ( isset( $entry_array['relatedlink']['url'] ) ) {
      // Show 'relatedlink' symbol - New to 0.4.6
      if ( $comment_area != "" ) {
          $comment_area  .= '&nbsp;|&nbsp;';
        }
      $comment_area  .= '<a href="' . $entry_array['relatedlink']['url'] . '">' . $entry_array['relatedlink']['name'] . '</a>' . "\n";
    }

    if ( isset( $entry_array[ 'stars_nototals' ] ) ) {
      // Show 'rating' stars...
      if ( $comment_area != "" ) {
          $comment_area  .= '&nbsp;|&nbsp;';
        }
      $comment_area  .= $entry_array[ 'stars_nototals' ] . "\n";
    }

    // Display EDIT and DELETE buttons if the user is logged in.
    if ( isset( $entry_array[ 'logged_in' ] ) && $entry_array[ 'logged_in' ] == true ) {
      // Show "edit" and "delete" buttons if the user is logged-in...
      if ( isset( $entry_array[ 'edit' ][ 'url' ] ) ) {
        if ( $comment_area != "" ) {
          $comment_area  .= '&nbsp;|&nbsp;';
        }
        $comment_area  .= "\t\t" . '<a href="' . $entry_array[ 'edit' ][ 'url' ] . '">' . $entry_array[ 'edit' ][ 'name' ] . '</a>' . "\n";
      }
      if ( isset( $entry_array[ 'delete' ][ 'url' ] ) ) {
        if ( $comment_area != "" ) {
          $comment_area  .= '&nbsp;|&nbsp;';
        }
        $comment_area  .= "\t\t" . '<a href="' . $entry_array[ 'delete' ][ 'url' ] . '">' . $entry_array[ 'delete' ][ 'name' ] . '</a>' . "\n";
      }
      if ( isset( $entry_array[ 'ban' ][ 'url' ] ) ) {
        if ( $comment_area != "" ) {
          $comment_area  .= '&nbsp;|&nbsp;';
        }
        $comment_area  .= "\t\t" . '<a href="' . $entry_array[ 'ban' ][ 'url' ] . '">' . $entry_array[ 'ban' ][ 'name' ] . '</a>' . "\n";
      }
    }

    if ( $comment_area != "" ) {
      $blog_content  .= "\n\t<div class=\"blog_comment\">" . $comment_area . "</div>\n";
    }

    $blog_content  .= "\n<!-- FOOTER ENTRY END -->\n";

    $blog_content  .= "\n<!-- BLOG ENTRY END -->\n";

    return $blog_content;
  }

  function theme_genericentry ( $entry_array, $style='normal' ) { // New 0.5.0
    global $user_colors, $blog_theme, $blog_config;

    // init vars
    $img_path = "themes/" . $blog_theme . "/images/";
    $blog_content = '';
    $blog_content = $blog_content . "\n<!-- STATIC NO HEADER ENTRY START -->\n";

    // Text of entry

    if ( $style == 'solid' ) {
      $blog_content = $blog_content . '<div class="blog_body_solid">' . "\n\t";
    } elseif ( $style == 'clear' ) {
      $blog_content = $blog_content . '<div class="blog_body_clear">' . "\n\t";
    } else {
      $blog_content = $blog_content . '<div class="blog_body_framed">' . "\n\t";
    }

    $blog_content = $blog_content . $entry_array[ 'entry' ];
    $blog_content = $blog_content . "\n\t</div>";

    $blog_content = $blog_content . "<br />";

    $blog_content = $blog_content . "\n<!-- STATIC NO HEADER ENTRY END -->\n";

    return $blog_content;
  }

  function theme_staticentry ( $entry_array ) {
    // Display STATIC entry page.
    //
    // This theme uses the same format for static entries as regular blog entries.
    $blog_content = theme_blogentry( $entry_array, 'static' );
    return $blog_content;
  }

  function theme_commententry ( $entry_array ) {
    // Display COMMENT entry page.
    //
    // This theme uses the same format for comment entries as regular blog entries.
    $blog_content = theme_blogentry( $entry_array, 'comment' );
    return $blog_content;
  }

  // New 0.3.8
  function theme_trackbackentry ( $entry_array ) {
    global $blog_config, $blog_theme, $user_colors;

    $img_path = 'themes/' . $blog_theme . '/images/';

    $blog_content = "";
    $blog_content  .= "\n" . '<!-- BLOG ENTRY BEGIN -->' . "\n";

    // SUBJECT
    $blog_content  .= '<div class="blog_title">' . $entry_array[ 'title' ] . "\n";

    if ( isset( $entry_array[ 'logged_in' ] ) && $entry_array[ 'logged_in' ] == true ) {
      $blog_content  .= "\t" . '<span class="blog_title_buttons">' . "\n";

      // Show 'delete' button if the user is logged-in...
      if ( isset( $entry_array[ 'delete' ][ 'url' ] ) ) {
        $blog_content  .= "\t\t" . '<a href="' . $entry_array[ 'delete' ][ 'url' ] . '">' . $entry_array[ 'delete' ][ 'name' ] . '</a>' . "\n";
      }

      $blog_content  .= "\t" . '</span>' . "\n";
    }

    $blog_content  .= '</div>' . "\n";

    $blog_content  .= '<div class="blog_body">' . "\n\t";
    $blog_content  .= '<div class="blog_date">' . $entry_array[ 'date' ] . '</div>' . "\n\t\t";

    // Blog content body...
    $blog_content  .= $entry_array[ 'excerpt' ] . "<p>\n";

    if ( (isset( $entry_array[ 'blog_name' ] ) ) && ($entry_array[ 'blog_name' ] != "") ) {
       $blog_content = $blog_content . '<a href="'.$entry_array[ 'url' ].'" target="_blank">[ ' . $entry_array[ 'blog_name' ] . " ]</a><p>\n";
    } else {
       $blog_content = $blog_content . '<a href="'.$entry_array[ 'url' ].'" target="_blank">[ ' . $entry_array[ 'url' ] . " ]</a><p>\n";
    }

    $blog_content  .= "\n\t" . '</div>';

    $blog_content  .= "<br />";

    $blog_content  .= "\n" . '<!-- BLOG ENTRY END -->' . "\n";

    return $blog_content;
  }

  // Function:
  // theme_default_colors( )
  //
  // Colors are locked in for this theme - they would have to be changed here

  function theme_default_colors () {
    global $lang_string;

    $color_def = array();

    // Backgrounds
    array_push( $color_def, array( 'id' => 'bg_color',
                'string' => $lang_string[ 'bg_color' ],
                'default' => 'FFFFFF' ) );
    array_push( $color_def, array( 'id' => 'header_bg_color',
                'string' => $lang_string[ 'header_bg_color' ],
                'default' => 'FFFFFF' ) );
    array_push( $color_def, array( 'id' => 'footer_bg_color',
                'string' => $lang_string[ 'footer_bg_color' ],
                'default' => 'FFFFFF' ) );
    array_push( $color_def, array( 'id' => 'main_bg_color',
                'string' => $lang_string[ 'main_bg_color' ],
                'default' => 'FFFFFF' ) );
    array_push( $color_def, array( 'id' => 'menu_bg_color',
                'string' => $lang_string[ 'menu_bg_color' ],
                'default' => 'FFFFFF' ) );

    // Borders
    array_push( $color_def, array( 'id' => 'border_color',
                'string' => $lang_string[ 'border_color' ],
                'default' => 'FFFFFF' ) );
    array_push( $color_def, array( 'id' => 'inner_border_color',
                'string' => $lang_string[ 'inner_border_color' ],
                'default' => 'FFFFFF' ) );
    array_push( $color_def, array( 'id' => 'header_txt_color',
                'string' => $lang_string[ 'header_txt_color' ],
                'default' => '000000' ) );
    array_push( $color_def, array( 'id' => 'footer_txt_color',
                'string' => $lang_string[ 'footer_txt_color' ],
                'default' => '000000' ) );
    array_push( $color_def, array( 'id' => 'txt_color',
                'string' => $lang_string[ 'txt_color' ],
                'default' => '737367' ) );
    array_push( $color_def, array( 'id' => 'headline_txt_color',
                'string' => $lang_string[ 'headline_txt_color' ],
                'default' => '737367' ) );
    array_push( $color_def, array( 'id' => 'date_txt_color',
                'string' => $lang_string[ 'date_txt_color' ],
                'default' => 'A6A695' ) );


    array_push( $color_def, array( 'id' => 'entry_bg',
                'string' => $lang_string[ 'entry_bg' ],
                'default' => 'FFFFFF' ) );
    array_push( $color_def, array( 'id' => 'entry_title_bg',
                'string' => $lang_string[ 'entry_title_bg' ],
                'default' => 'FFFFFF' ) );
    array_push( $color_def, array( 'id' => 'entry_border',
                'string' => $lang_string[ 'entry_border' ],
                'default' => 'A6A695' ) );

    array_push( $color_def, array( 'id' => 'entry_title_text',
                'string' => $lang_string[ 'entry_title_text' ],
                'default' => '000000' ) );
    array_push( $color_def, array( 'id' => 'entry_text',
                'string' => $lang_string[ 'entry_text' ],
                'default' => '000000' ) );

    array_push( $color_def, array( 'id' => 'menu_bg',
                'string' => $lang_string[ 'menu_bg' ],
                'default' => 'FFFFFF' ) );
    array_push( $color_def, array( 'id' => 'menu_title_bg',
                'string' => $lang_string[ 'menu_title_bg' ],
                'default' => 'FFFFFF' ) );
    array_push( $color_def, array( 'id' => 'menu_border',
                'string' => $lang_string[ 'menu_border' ],
                'default' => 'FFFFFF' ) );

    array_push( $color_def, array( 'id' => 'menu_title_text',
                'string' => $lang_string[ 'menu_title_text' ],
                'default' => '000000' ) );
    array_push( $color_def, array( 'id' => 'menu_text',
                'string' => $lang_string[ 'menu_text' ],
                'default' => '000000' ) );

    array_push( $color_def, array( 'id' => 'link_reg_color',
                'string' => $lang_string[ 'link_reg_color' ],
                'default' => '59594F' ) );
    array_push( $color_def, array( 'id' => 'link_hi_color',
                'string' => $lang_string[ 'link_hi_color' ],
                'default' => 'FF6600' ) );
    array_push( $color_def, array( 'id' => 'link_down_color',
                'string' => $lang_string[ 'link_down_color' ],
                'default' => '6329F7' ) );

    array_push( $color_def, array( 'id' => 'menu_link_reg_color',
                'string' => $lang_string[ 'menu_link_reg_color' ],
                'default' => '59594F' ) );
    array_push( $color_def, array( 'id' => 'menu_link_hi_color',
                'string' => $lang_string[ 'menu_link_hi_color' ],
                'default' => 'FF6600' ) );
    array_push( $color_def, array( 'id' => 'menu_link_down_color',
                'string' => $lang_string[ 'menu_link_down_color' ],
                'default' => '6329F7' ) );

    return ( $color_def );
  }

  // Function:
  // theme_pagelayout( )
  //
  // Page Layout Container/Wrapper
  // -----------------------------
  // This function controls all HTML output to the browser.
  //
  // Invoking the page_content() fuction inserts the actual
  // contents of the page.
  //
  function theme_pagelayout () {
    global $user_colors, $blog_theme, $blog_config, $theme_vars;

    // New 0.3.8
    //
    // You can change the width of the CONTENT area and MENU areas by
    // editing these variable in the "theme_init()" function at the top of
    // this file.
    $content_width = $theme_vars[ 'content_width' ];
    $menu_width = $theme_vars[ 'menu_width' ];
    $page_width = $content_width + $menu_width;

    // Default image path.
    $img_path = "themes/" . $blog_theme . "/images/";

    // Begin Page Layout HTML
    ?>
    <body>
      <div id="page">
          <div id="header">
               <div id="HeaderLeft"></div>
               <div id="HeaderCenter">

               <?php if ( $blog_config->getTag('BLOG_ENABLE_TITLE')){// New for 0.4.6
                  echo( '<a href="index.php">' . $blog_config->getTag('BLOG_TITLE') . '</a>' );} ?>
               </div>
               <div id="HeaderRight"></div>
           </div>
          <div id="pagebody">
                    <div id="sidebar">
                      <?php theme_menu(); ?>
                    </div>
                    <div id="maincontent">
                      <?php page_content(); ?>
                    </div>
                    <div id="footer"><p><?php echo($blog_config->getTag('BLOG_FOOTER')); ?> - <?php echo( page_generated_in() ); ?></p></div>
           </div>
      </div>
    </body>
    <?php
    // End Page Layout HTML
  }

  function theme_menu () {
    global $user_colors, $lang_string, $theme_vars, $logged_in, $sb_info, $blog_config;

    // This function determines the content displayed
    // in the menu area on the right.
    //
    // Feel free to move these blocks of code around.
    //
    // For instance, if you want the calendar at the
    // top of the menu area, just copy the ARCHIVE
    // block of code to the top (above LINKS.)
    //
    // The "\n" that you see is a RETURN character.
    // This is just to make the HTML code look prettier.
    // It will not show up on the page.
    //
    // Please note that \n must be used within " quotes...
    // echo( "\n" ); // <-- This is a return character
    // echo( '\n' ); // <-- This will print \n on your page...
    //
    // You can use either ' or " in your echo() statements.
    // But keep in mind that might need to use a backslash --> \
    // to print a double or single quote:
    //
    // These are equivatent: (note the \" escape chracter...)
    // echo( 'this "is" a test' );
    // echo( "this \"is\" a test" );
    //
    // These are equivatent: (note the \' escape chracter...)
    // echo( 'this \'is\' a test' );
    // echo( "this 'is' a test" );
    //
    // I could go on, but this is kind of boring stuff... :)

    echo( "\n<!-- SIDEBAR MENU BEGIN -->\n" );

    // New 0.3.8
    //
    // The ADD BLOCKS page lets you insert your own
    // content "blocks" into the menu area.
    $array = read_blocks($logged_in);
    for($i=0 ; $i<count($array) ; $i+=2) {
      if ( $array[$i+1] != "" ) {
        echo("<div class=\"menu_title\">" . $array[$i] . "</div>\n" );
        echo( "<div class=\"menu_body\">\n" );
        echo( $array[$i+1] . "\n" );
        echo( "</div><br />\n" );
      }
    }
  }

?>
