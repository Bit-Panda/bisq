/*
 * This file is part of bisq.
 *
 * bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package io.bisq.message.trade;

import com.google.protobuf.ByteString;
import io.bisq.app.Version;
import io.bisq.common.wire.proto.Messages;
import io.bisq.message.p2p.MailboxMessage;
import io.bisq.payload.NodeAddress;
import io.bisq.proto.ProtoBufferUtils;

import javax.annotation.concurrent.Immutable;
import java.util.Arrays;
import java.util.UUID;

@Immutable
public final class PayoutTxFinalizedMessage extends TradeMessage implements MailboxMessage {
    // That object is sent over the wire, so we need to take care of version compatibility.
    private static final long serialVersionUID = Version.P2P_NETWORK_VERSION;

    public final byte[] payoutTx;
    private final NodeAddress senderNodeAddress;
    private final String uid;

    public PayoutTxFinalizedMessage(String tradeId, byte[] payoutTx, NodeAddress senderNodeAddress) {
        this(tradeId, payoutTx, senderNodeAddress, UUID.randomUUID().toString());
    }

    public PayoutTxFinalizedMessage(String tradeId, byte[] payoutTx, NodeAddress senderNodeAddress, String uid) {
        super(tradeId);
        this.payoutTx = payoutTx;
        this.senderNodeAddress = senderNodeAddress;
        this.uid = uid;
    }

    @Override
    public NodeAddress getSenderNodeAddress() {
        return senderNodeAddress;
    }

    @Override
    public String getUID() {
        return uid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PayoutTxFinalizedMessage)) return false;
        if (!super.equals(o)) return false;

        PayoutTxFinalizedMessage that = (PayoutTxFinalizedMessage) o;

        if (!Arrays.equals(payoutTx, that.payoutTx)) return false;
        if (senderNodeAddress != null ? !senderNodeAddress.equals(that.senderNodeAddress) : that.senderNodeAddress != null)
            return false;
        return !(uid != null ? !uid.equals(that.uid) : that.uid != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (payoutTx != null ? Arrays.hashCode(payoutTx) : 0);
        result = 31 * result + (senderNodeAddress != null ? senderNodeAddress.hashCode() : 0);
        result = 31 * result + (uid != null ? uid.hashCode() : 0);
        return result;
    }

    @Override
    public Messages.Envelope toProtoBuf() {
        Messages.Envelope.Builder baseEnvelope = ProtoBufferUtils.getBaseEnvelope();
        return baseEnvelope.setPayoutTxFinalizedMessage(baseEnvelope.getPayoutTxFinalizedMessageBuilder()
                .setUid(uid)
                .setMessageVersion(getMessageVersion())
                .setTradeId(tradeId)
                .setPayoutTx(ByteString.copyFrom(payoutTx))
                .setSenderNodeAddress(senderNodeAddress.toProtoBuf())).build();
    }
}
