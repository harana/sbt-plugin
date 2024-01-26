package com.harana.sbt.common

import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import sbt._

object Dependencies {

  val compilerPlugins = Vector(
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    addCompilerPlugin("org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
  )

  val zio = "2.0.21"

  val common = Def.setting { 
    Seq(
      "io.github.cquiroz"                 %%%     "scala-java-time"                 % "2.3.0",
      "org.scalaz" 			                  %%%     "scalaz-core"                     % "7.3.8",
      "dev.zio"                            %%     "zio"                             % zio,
      "dev.zio"                            %%     "zio-interop-cats"                % "23.1.0.0",
      "dev.zio"                            %%     "zio-process"                     % "0.7.2",
      "dev.zio"                            %%     "zio-streams"                     % zio,
      "dev.zio"                            %%     "zio-test"                        % zio % "test",
      "dev.zio"                            %%     "zio-test-sbt"                    % zio % "test",
      "dev.zio"                            %%     "zio-test-magnolia"               % zio % "test"
    )
  }

  val slinky = "0.7.4"
  val scalajs = "1.15.0"

  val js = Def.setting {
    Seq(
      "me.shadaj"                         %%%   	"slinky-core"               			% slinky,
      "me.shadaj"                         %%%   	"slinky-web"                			% slinky,
      "me.shadaj"                         %%%   	"slinky-history"          			  % slinky,
      "me.shadaj"                         %%%   	"slinky-hot"                			% slinky,
      "me.shadaj"                         %%%   	"slinky-react-router"          	  % slinky,
      "me.shadaj"                         %%%   	"slinky-readwrite"          			% slinky
    )
  }
  
  val jsOverrides = Def.setting {
    Seq(
		  "org.scala-js" 								        % 	  "sbt-scalajs" 										% scalajs
    )
  }

  val jvm = Def.setting {
    Seq(
      "com.vmunier"                        %%     "scalajs-scripts"                 % "1.1.4",
      "commons-io"                          %     "commons-io"                      % "2.6",
      "org.apache.commons"                  %     "commons-text"                    % "1.4",
      "org.javassist"                       %     "javassist"                       % "3.23.0-GA"
    )
  }

  val jvmExcludes = Def.setting {
    Seq(
      "org.slf4j"                          %      "log4j-over-slf4j"
    )
  }

  val jvmOverrides = Def.setting {
    Seq(
      "org.ow2.asm"                        %      "asm"                            % "6.0",
      "org.ow2.asm"                        %      "asm-commons"                    % "6.0",
      "org.ow2.asm"                        %      "asm-tree"                       % "6.0",
      "com.google.code.findbugs"           %      "jsr305"                         % "3.0.0",
      "org.osgi"                           %      "org.osgi.core"                  % "6.0.0",
      "org.codehaus.plexus"                %      "plexus-utils"                   % "3.0.22",
      "org.scala-js"                       %      "sbt-scalajs"                    % scalajs,
      "org.webjars"                        %      "webjars-locator-core"           % "0.33",
      "org.eclipse.sisu"                   %      "org.eclipse.sisu.plexus"        % "0.3.2",
      "org.eclipse.sisu"                   %      "org.eclipse.sisu.inject"        % "0.3.2"
    )
  }

  val macros = Def.setting {
    Seq(
      "org.scala-lang"                    %       "scala-compiler"                 % "2.12.15" % "provided",
      "org.scala-lang"                    %       "scala-reflect"                  % "2.12.15"
    )
  }
  
  val npm = Def.setting {
    Seq(    
    )
  }

  val npmDev = Def.setting { 
    Seq(    
      "awesome-typescript-loader"				  	        ->    "5.2.1",
      "electron-builder"                            ->    "22.10.5",
      "hard-source-webpack-plugin"				          ->    "0.13.1",
      "html-webpack-plugin"                       	->    "4.5.2",
      "static-site-generator-webpack-plugin" 	 	    ->    "3.4.2",
      "typescript"							  	                ->    "4.2.3",
      "webpack-merge" 							                ->    "5.7.3"
    )
  }

  val osgi = Def.setting { 
    Seq(
      "org.apache.felix"                   % "org.apache.felix.fileinstall"         % "3.6.4",
      "org.apache.felix"                   % "org.apache.felix.configadmin"         % "1.8.16",
      "org.apache.felix"                   % "org.apache.felix.bundlerepository"    % "2.0.10"    
    )
  }
}
