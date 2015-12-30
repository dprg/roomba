::
:: Builds the distribution zip file that contains a properly formatted 
:: Processing library.  Unzip the resulting file in the 'libraries' directory
:: in Processing to install.
::
:: Before running this script, run 'build-jar.sh'
::

cd packaging
rd /s /q processing
md processing\roombacomm\library
copy ..\roombacomm.jar      processing\roombacomm\library\roombacomm.jar
copy processing-export.txt  processing\roombacomm\library\export.txt
copy ..\rxtxlib\*           processing\roombacomm\library
md processing\roombacomm\examples
xcopy ..\processing-examples processing\roombacomm\examples /s 
copy ..\README              processing\roombacomm\README
copy processing-readme.txt  processing\roombacomm\README-processing.txt
cd processing
..\..\7z a roombacomm-processing.zip roombacomm -r
copy roombacomm-processing.zip ..\..
cd ..\..
