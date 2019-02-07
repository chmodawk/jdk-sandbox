/*
 * Copyright (c) 2018, 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package jdk.net;

import java.net.ProtocolFamily;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.StandardProtocolFamily;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.io.IOException;
import java.util.Objects;
import jdk.internal.net.rdma.RdmaPollSelectorProvider;
import jdk.internal.net.rdma.RdmaSocketProvider;

/**
 * This class defines static factory methods to create sockets and channels to
 * RDMA-based sockets, and selectors to multiplex channels to RDMA sockets.
 *
 * <p> The {@linkplain #openSocket(ProtocolFamily) openSocket} and {@linkplain
 * #openServerSocket(ProtocolFamily) openServerSocket} methods create RDMA-based
 * TCP sockets.
 *
 * <p> The {@linkplain #openSocketChannel(ProtocolFamily) openSocketChannel} and
 * {@linkplain #openServerSocketChannel(ProtocolFamily) openServerSocketChannel}
 * methods open {@link java.nio.channels.SelectableChannel selectable channels}
 * to RDMA sockets. The {@linkplain #openSelector() openSelector} opens a
 * {@linkplain Selector} for multiplexing selectable channels to RDMA sockets.
 * Selectable channels to RDMA sockets can not be multiplexed with selectable
 * channels opened by the default system-wide {@link SelectorProvider selector
 * provider}. Furthermore, the RDMA selector provider does not support datagram
 * channels or pipes; its {@linkplain SelectorProvider#openDatagramChannel
 * openDatagramChannel} and {@linkplain SelectorProvider#openPipe openPipen}
 * methods throw {@code UnsupportedOperationException}.
 *
 * <p> Unless otherwise noted, passing a {@code null} argument will cause a
 * {@linkplain NullPointerException} to be thrown.
 *
 * @implNote The RDMA selector provider supports channels to both IPv4 and
 * IPv6 RDMA sockets. If the no-arg {@linkplain SelectorProvider#openSocketChannel()
 * openSocketChannel} or {@linkplain SelectorProvider#openServerSocketChannel()
 * openServerSocketChannel} methods are used to open channels then those channels
 * will be to IPv6 sockets if IPv6 is enabled on the platform (or IPv4 sockets
 * if IPv6 is not enabled).
 *
 * @since 13
 */
public class RdmaSockets {

    private RdmaSockets() {}

    /**
     * Creates an unbound and unconnected RDMA socket.
     *
     * <p> An RDMA socket supports the socket options
     * {@link java.net.StandardSocketOptions#SO_RCVBUF SO_RCVBUF},
     * {@link java.net.StandardSocketOptions#SO_REUSEADDR SO_REUSEADDR},
     * {@link java.net.StandardSocketOptions#SO_SNDBUF SO_SNDBUF},
     * {@link java.net.StandardSocketOptions#TCP_NODELAY TCP_NODELAY},
     * and those specified by {@link RdmaSocketOptions}.
     *
     * <p> When {@link Socket#bind binding} the socket to a local address, or
     * {@link Socket#connect connecting} the socket, the socket address specified
     * to the {@code bind} and {@code connect} methods must correspond to the
     * protocol family specified here.
     *
     * @param  family
     *         The protocol family
     *
     * @throws IOException
     *         If an I/O error occurs
     * @throws UnsupportedOperationException
     *         If RDMA sockets are not supported on this platform or if the
     *         specified protocol family is not supported. For example, if
     *         the parameter is {@link java.net.StandardProtocolFamily#INET6
     *         StandardProtocolFamily.INET6} but IPv6 is not enabled on the
     *         platform.
     */
    public static Socket openSocket(ProtocolFamily family) throws IOException {
        Objects.requireNonNull(family, "protocol family is null");
        return RdmaSocketProvider.openSocket(family);
    }

    /**
     * Creates an unbound RDMA server socket.
     *
     * <p> An RDMA server socket supports the socket options
     * {@link java.net.StandardSocketOptions#SO_RCVBUF SO_RCVBUF},
     * {@link java.net.StandardSocketOptions#SO_REUSEADDR SO_REUSEADDR},
     * and those specified by {@link RdmaSocketOptions}.
     *
     * <p> When binding the socket to an address, the socket address specified
     * to the {@linkplain ServerSocket#bind bind} method must correspond to the
     * protocol family specified here.
     *
     * @param family
     *        The protocol family
     *
     * @throws IOException
     *         If an I/O error occurs
     * @throws UnsupportedOperationException
     *         If RDMA sockets are not supported on this platform or if the
     *         specified protocol family is not supported. For example, if
     *         the parameter is {@link java.net.StandardProtocolFamily#INET6
     *         StandardProtocolFamily.INET6} but IPv6 is not enabled on the
     *         platform.
     */
    public static ServerSocket
    openServerSocket(ProtocolFamily family) throws IOException {
        Objects.requireNonNull(family, "protocol family is null");
        return RdmaSocketProvider.openServerSocket(family);
    }

    /**
     * Opens a socket channel to an RDMA socket. A newly created socket channel
     * is {@link java.nio.channels.Channel#isOpen() open} but not yet {@link
     * SocketChannel#bind bound} or {@link SocketChannel#isConnected() connected}.
     *
     * <p> A socket channel to an RDMA socket supports the socket options
     * {@link java.net.StandardSocketOptions#SO_RCVBUF SO_RCVBUF},
     * {@link java.net.StandardSocketOptions#SO_REUSEADDR SO_REUSEADDR},
     * {@link java.net.StandardSocketOptions#SO_SNDBUF SO_SNDBUF},
     * {@link java.net.StandardSocketOptions#TCP_NODELAY TCP_NODELAY},
     * and those specified by {@link RdmaSocketOptions}.
     *
     * <p> When binding the channel's socket to a local address, or {@link
     * SocketChannel#connect connecting} the channel's socket, the socket address
     * specified to the {@code bind} and {@code connect} methods must correspond
     * to the protocol family specified here.
     *
     * @param  family
     *         The protocol family
     *
     * @throws IOException
     *         If an I/O error occurs
     * @throws UnsupportedOperationException
     *         If RDMA sockets are not supported on this platform or if the
     *         specified protocol family is not supported. For example, if
     *         the parameter is {@link java.net.StandardProtocolFamily#INET6
     *         StandardProtocolFamily.INET6} but IPv6 is not enabled on the
     *         platform.
     */
    public static SocketChannel
    openSocketChannel(ProtocolFamily family) throws IOException {
        Objects.requireNonNull(family, "protocol family is null");
        return RdmaPollSelectorProvider.provider().openSocketChannel(family);
    }

    /**
     * Opens a server-socket channel to an RDMA socket. A newly created
     * server-socket is {@link java.nio.channels.Channel#isOpen() open} but not
     * yet {@link ServerSocketChannel#bind bound}.
     *
     * <p> A server-socket channel to an RDMA server socket supports the socket
     * options {@link java.net.StandardSocketOptions#SO_RCVBUF SO_RCVBUF},
     * {@link java.net.StandardSocketOptions#SO_REUSEADDR SO_REUSEADDR},
     * and those specified by {@link RdmaSocketOptions}.
     *
     * <p> When binding the channel's socket to a local address, the address
     * specified to the {@linkplain ServerSocketChannel#bind bind} method must
     * correspond to the protocol family specified here.
     *
     * @param  family
     *         The protocol family
     *
     * @throws IOException
     *         If an I/O error occurs
     * @throws UnsupportedOperationException
     *         If RDMA sockets are not supported on this platform or the
     *         specified protocol family is not supported. For example, if
     *         the parameter is {@link java.net.StandardProtocolFamily#INET6
     *         StandardProtocolFamily.INET6} but IPv6 is not enabled on the
     *         platform.
     */
    public static ServerSocketChannel
    openServerSocketChannel(ProtocolFamily family) throws IOException {
        Objects.requireNonNull(family, "protocol family is null");
        return RdmaPollSelectorProvider.provider().openServerSocketChannel(family);
    }

    /**
     * Opens a selector to multiplex selectable channels to RDMA sockets.
     *
     * @throws IOException
     *         If an I/O error occurs
     * @throws UnsupportedOperationException
     *         If RDMA sockets are not supported on this platform
     */
    public static Selector openSelector() throws IOException {
        return RdmaPollSelectorProvider.provider().openSelector();
    }
}
