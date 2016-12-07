/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/openmobster/projects/openmobster/private/android/trunk/mobileCloud/android/2_0/test-suite/test-bus-port/src/main/java/org/openmobster/core/mobileCloud/android/module/bus/rpc/IBusHandler.aidl
 */
package org.openmobster.core.mobileCloud.android.module.bus.rpc;
public interface IBusHandler extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements org.openmobster.core.mobileCloud.android.module.bus.rpc.IBusHandler
{
private static final java.lang.String DESCRIPTOR = "org.openmobster.core.mobileCloud.android.module.bus.rpc.IBusHandler";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an org.openmobster.core.mobileCloud.android.module.bus.rpc.IBusHandler interface,
 * generating a proxy if needed.
 */
public static org.openmobster.core.mobileCloud.android.module.bus.rpc.IBusHandler asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof org.openmobster.core.mobileCloud.android.module.bus.rpc.IBusHandler))) {
return ((org.openmobster.core.mobileCloud.android.module.bus.rpc.IBusHandler)iin);
}
return new org.openmobster.core.mobileCloud.android.module.bus.rpc.IBusHandler.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_handleInvocation:
{
data.enforceInterface(DESCRIPTOR);
java.util.Map _arg0;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
java.util.Map _result = this.handleInvocation(_arg0);
reply.writeNoException();
reply.writeMap(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements org.openmobster.core.mobileCloud.android.module.bus.rpc.IBusHandler
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public java.util.Map handleInvocation(java.util.Map invocation) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.Map _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(invocation);
mRemote.transact(Stub.TRANSACTION_handleInvocation, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_handleInvocation = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public java.util.Map handleInvocation(java.util.Map invocation) throws android.os.RemoteException;
}
