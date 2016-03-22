# torrent-server

[![Build Status](https://travis-ci.org/MikaelSmith/torrent-server.svg?branch=master)](https://travis-ci.org/MikaelSmith/torrent-server)

A simple torrent file server built on [puppetlabs/trapperkeeper](https://github.com/puppetlabs/trapperkeeper).
It aims to make it simple to serve large files to many clients quickly and with
low-overhead on the server compared to traditional file servers.

An on-demand torrent server: it serves files out of a (configurable) directory,
with the file name derived from the URL path. It sets up a BitTorrent tracker;
then on request ensures a `.torrent` file exists for a requested file (if the file
exists), announces it to the tracker, starts seeding the file, and returns the
`.torrent` file to the caller in the response. That `.torrent` file can be used
to start downloading the file via the BitTorrent protocol.

Communication is currently unauthenticated and transfers done over HTTP.

## Usage

First, run:

    $ lein tk

Then, `curl http://localhost:8080/torrent/test-file -o test-file.torrent` to
start seeding [`test-file`](./dev-resources/test-file) and get the appropriate
torrent file. That torrent file can then be downloaded via an appropriate
torrent client, such as examples provided along-side [libtorrent](http://www.libtorrent.org/).

### Configuration

An example configuration is present in [dev-resources/config.conf](./dev-resources/config.conf).
It contains generic trapperkeeper config, as well as a `torrent-server` section
- `file-source`: a relative or absolute path to the directory from which to serve files
- `tracker-port`: the port to use for the BitTorrent tracker

### Running from the REPL

Alternately, run:

    $ lein repl
    nREPL server started on port 52137 on host 127.0.0.1
    user => (go)

This will allow you to launch the app from the Clojure REPL. You can then make
changes and run `(reset)` to reload the app or `(stop)` to shutdown the app.

In addition, the functions `(context)` and `(print-context)` are available to
print out the current trapperkeeper application context. Both of these take an
optional array of keys as a parameter, which is used to retrieve a nested
subset of the context map.

## Future Improvements

- Search file source on service start to generate a finite list of Clients, so we can clean them up on shutdown.
- Use HTTPS
- Use trapperkeeper-authorization

## License

Copyright © 2016 Michael Smith

Distributed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
