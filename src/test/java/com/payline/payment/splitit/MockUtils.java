package com.payline.payment.splitit;

import com.payline.payment.splitit.utils.Constants;
import com.payline.payment.splitit.utils.http.StringResponse;
import com.payline.pmapi.bean.common.Buyer;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.notification.request.NotificationRequest;
import com.payline.pmapi.bean.payment.*;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormLogoRequest;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.reset.request.ResetRequest;
import org.apache.http.Header;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicStatusLine;
import org.mockito.internal.util.reflection.FieldSetter;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class MockUtils {
    private static String TRANSACTIONID = "123456789012345678901";
    private static String PARTNER_TRANSACTIONID = "098765432109876543210";


    /**------------------------------------------------------------------------------------------------------------------*/

    /**
     * Generate a valid {@link Environment}.
     */
    public static Environment anEnvironment() {
        return new Environment("https://example.org/store/notification",
                "https://example.org/store/redirection",
                "http://redirectionCancelURL.com",
                true);
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Generate a valid {@link PartnerConfiguration}.
     */
    public static PartnerConfiguration aPartnerConfiguration() {
        Map<String, String> partnerConfigurationMap = new HashMap<>();
        partnerConfigurationMap.put(Constants.PartnerConfigurationKeys.URL, "https://web-api-sandbox.splitit.com");
        partnerConfigurationMap.put(Constants.PartnerConfigurationKeys.API_KEY, "f661600c-5f1a-4d4c-829d-768fbc40be6c");
        Map<String, String> sensitiveConfigurationMap = new HashMap<>();
        return new PartnerConfiguration(partnerConfigurationMap, sensitiveConfigurationMap);
    }
    /**------------------------------------------------------------------------------------------------------------------*/


    /**
     * Generate a valid {@link PaymentFormConfigurationRequest}.
     */
    public static PaymentFormConfigurationRequest aPaymentFormConfigurationRequest() {
        return aPaymentFormConfigurationRequestBuilder().build();
    }

    /**
     * Generate a builder for a valid {@link PaymentFormConfigurationRequest}.
     * This way, some attributes may be overridden to match specific test needs.
     */
    public static PaymentFormConfigurationRequest.PaymentFormConfigurationRequestBuilder aPaymentFormConfigurationRequestBuilder() {
        return PaymentFormConfigurationRequest.PaymentFormConfigurationRequestBuilder.aPaymentFormConfigurationRequest()
                .withAmount(aPaylineAmount())
                .withBuyer(aBuyer())
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withLocale(Locale.FRANCE)
                .withOrder(aPaylineOrder())
                .withPartnerConfiguration(aPartnerConfiguration());
    }

    /**
     * Generate a valid {@link PaymentFormLogoRequest}.
     */
    public static PaymentFormLogoRequest aPaymentFormLogoRequest() {
        return PaymentFormLogoRequest.PaymentFormLogoRequestBuilder.aPaymentFormLogoRequest()
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withPartnerConfiguration(aPartnerConfiguration())
                .withLocale(Locale.getDefault())
                .build();
    }

    /**
     * Generate a valid, but not complete, {@link Order}
     */
    public static Order aPaylineOrder() {
        List<Order.OrderItem> items = new ArrayList<>();

        items.add(Order.OrderItem.OrderItemBuilder
                .anOrderItem()
                .withReference("foo")
                .withAmount(aPaylineAmount())
                .withQuantity((long) 1)
                .build());

        return Order.OrderBuilder.anOrder()
                .withDate(new Date())
                .withAmount(aPaylineAmount())
                .withItems(items)
                .withReference("ref-20191105153749")
                .build();
    }

    /**
     * Generate a valid Payline Amount.
     */
    public static com.payline.pmapi.bean.common.Amount aPaylineAmount() {
        return aPaylineAmount(1234);
    }

    public static com.payline.pmapi.bean.common.Amount aPaylineAmount(int amount) {
        return new com.payline.pmapi.bean.common.Amount(BigInteger.valueOf(amount), Currency.getInstance("EUR"));
    }

    /**
     * @return a valid user agent.
     */
    public static String aUserAgent() {
        return "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:67.0) Gecko/20100101 Firefox/67.0";
    }

    /**
     * Generate a valid {@link Browser}.
     */
    public static Browser aBrowser() {
        return Browser.BrowserBuilder.aBrowser()
                .withLocale(Locale.getDefault())
                .withIp("192.168.0.1")
                .withUserAgent(aUserAgent())
                .build();
    }

    /**
     * Generate a valid {@link Buyer}.
     */
    public static Buyer aBuyer() {
        return Buyer.BuyerBuilder.aBuyer()
                .withFullName(new Buyer.FullName("Marie", "Durand", "1"))
                .withBirthday(new Date())
                .withAddresses(addresses())
                .withPhoneNumbers(phoneNumbers())
                .withEmail("foo@bar.baz")
                .build();
    }

    public static Map<Buyer.AddressType, Buyer.Address> addresses() {
        Map<Buyer.AddressType, Buyer.Address> addresses = new HashMap<>();
        addresses.put(Buyer.AddressType.BILLING, anAddress());
        addresses.put(Buyer.AddressType.DELIVERY, anAddress());

        return addresses;
    }

    public static Buyer.Address anAddress() {
        return Buyer.Address.AddressBuilder
                .anAddress()
                .withStreet1("street1")
                .withStreet2("street2")
                .withCity("City")
                .withZipCode("75000")
                .withState("France")
                .build();
    }

    public static Map<Buyer.PhoneNumberType, String> phoneNumbers() {
        Map<Buyer.PhoneNumberType, String> phoneNumbers = new HashMap<>();
        phoneNumbers.put(Buyer.PhoneNumberType.HOME, "0612345678");
        phoneNumbers.put(Buyer.PhoneNumberType.WORK, "0712345678");
        phoneNumbers.put(Buyer.PhoneNumberType.CELLULAR, "0612345678");
        phoneNumbers.put(Buyer.PhoneNumberType.BILLING, "0612345678");

        return phoneNumbers;
    }

    /**
     * Generate a valid {@link PaymentFormContext}.
     */
    public static PaymentFormContext aPaymentFormContext() {
        Map<String, String> paymentFormParameter = new HashMap<>();

        return PaymentFormContext.PaymentFormContextBuilder.aPaymentFormContext()
                .withPaymentFormParameter(paymentFormParameter)
                .withSensitivePaymentFormParameter(new HashMap<>())
                .build();
    }

    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Generate a valid {@link ContractParametersCheckRequest}.
     */
    public static ContractParametersCheckRequest aContractParametersCheckRequest() {
        return aContractParametersCheckRequestBuilder().build();
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Generate a builder for a valid {@link ContractParametersCheckRequest}.
     * This way, some attributes may be overridden to match specific test needs.
     */
    public static ContractParametersCheckRequest.CheckRequestBuilder aContractParametersCheckRequestBuilder() {
        return ContractParametersCheckRequest.CheckRequestBuilder.aCheckRequest()
                .withAccountInfo(anAccountInfo())
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withLocale(Locale.getDefault())
                .withPartnerConfiguration(aPartnerConfiguration());
    }

    /**
     * Generate a valid {@link PaymentRequest}.
     */
    public static PaymentRequest aPaylinePaymentRequest() {
        return aPaylinePaymentRequestBuilder().build();
    }

    /**
     * Generate a builder for a valid {@link PaymentRequest}.
     * This way, some attributes may be overridden to match specific test needs.
     */
    public static PaymentRequest.Builder aPaylinePaymentRequestBuilder() {
        return PaymentRequest.builder()
                .withAmount(aPaylineAmount())
                .withBrowser(aBrowser())
                .withBuyer(aBuyer())
                .withCaptureNow(true)
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withLocale(Locale.getDefault())
                .withOrder(aPaylineOrder())
                .withPartnerConfiguration(aPartnerConfiguration())
                .withPaymentFormContext(aPaymentFormContext())
                .withSoftDescriptor("softDescriptor")
                .withTransactionId(TRANSACTIONID);
    }

    public static RefundRequest aPaylineRefundRequest() {
        return aPaylineRefundRequestBuilder().build();
    }

    public static RefundRequest.RefundRequestBuilder aPaylineRefundRequestBuilder() {
        return RefundRequest.RefundRequestBuilder.aRefundRequest()
                .withAmount(aPaylineAmount())
                .withOrder(aPaylineOrder())
                .withBuyer(aBuyer())
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withTransactionId(TRANSACTIONID)
                .withPartnerTransactionId(PARTNER_TRANSACTIONID)
                .withPartnerConfiguration(aPartnerConfiguration());
    }


    public static ResetRequest aPaylineResetRequest() {
        return aPaylineResetRequestBuilder().build();
    }

    public static ResetRequest.ResetRequestBuilder aPaylineResetRequestBuilder() {
        return ResetRequest.ResetRequestBuilder.aResetRequest()
                .withAmount(aPaylineAmount())
                .withOrder(aPaylineOrder())
                .withBuyer(aBuyer())
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withTransactionId(TRANSACTIONID)
                .withPartnerTransactionId(PARTNER_TRANSACTIONID)
                .withPartnerConfiguration(aPartnerConfiguration());
    }

    public static NotificationRequest aPaylineNotificationRequest() {
        return aPaylineNotificationRequestBuilder().build();
    }

    public static NotificationRequest.NotificationRequestBuilder aPaylineNotificationRequestBuilder() {
        return NotificationRequest.NotificationRequestBuilder.aNotificationRequest()
                .withHeaderInfos(new HashMap<>())
                .withPathInfo("transactionDeId=1234567890123")
                .withHttpMethod("POST")
                .withContractConfiguration(aContractConfiguration())
                .withPartnerConfiguration(aPartnerConfiguration())
                .withContent(new ByteArrayInputStream("".getBytes()))
                .withEnvironment(anEnvironment());
    }


    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Generate a valid accountInfo, an attribute of a {@link ContractParametersCheckRequest} instance.
     */
    public static Map<String, String> anAccountInfo() {
        return anAccountInfo(aContractConfiguration());
    }
    /**------------------------------------------------------------------------------------------------------------------*/

    /**
     * Generate a valid accountInfo, an attribute of a {@link ContractParametersCheckRequest} instance,
     * from the given {@link ContractConfiguration}.
     *
     * @param contractConfiguration The model object from which the properties will be copied
     */
    public static Map<String, String> anAccountInfo(ContractConfiguration contractConfiguration) {
        Map<String, String> accountInfo = new HashMap<>();
        for (Map.Entry<String, ContractProperty> entry : contractConfiguration.getContractProperties().entrySet()) {
            accountInfo.put(entry.getKey(), entry.getValue().getValue());
        }
        return accountInfo;
    }

    /**
     * Generate a valid {@link ContractConfiguration}.
     */
    public static ContractConfiguration aContractConfiguration() {
        Map<String, ContractProperty> contractProperties = new HashMap<>();
        contractProperties.put(Constants.ContractConfigurationKeys.USERNAME, new ContractProperty("monextTest"));
        contractProperties.put(Constants.ContractConfigurationKeys.PASSWORD, new ContractProperty("eZ7HJddV"));

        return new ContractConfiguration("SplitIt", contractProperties);
    }

    /**
     * Generate a valid {@link RedirectionPaymentRequest}.
     */
    public static RedirectionPaymentRequest aRedirectionPaymentRequest() {
        return RedirectionPaymentRequest.builder()
                .withAmount(aPaylineAmount())
                .withBrowser(aBrowser())
                .withBuyer(aBuyer())
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withOrder(aPaylineOrder())
                .withPartnerConfiguration(aPartnerConfiguration())
                .withTransactionId(TRANSACTIONID)
                .build();
    }

    /**
     * Generate a valid {@link TransactionStatusRequest}.
     */
    public static TransactionStatusRequest aTransactionStatusRequest() {
        return TransactionStatusRequest.TransactionStatusRequestBuilder.aNotificationRequest()
                .withAmount(aPaylineAmount())
                .withBuyer(aBuyer())
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withOrder(aPaylineOrder())
                .withPartnerConfiguration(aPartnerConfiguration())
                .withTransactionId(PARTNER_TRANSACTIONID)
                .build();
    }


    /**
     * Moch a StringResponse with the given elements.
     *
     * @param statusCode    The HTTP status code (ex: 200, 403)
     * @param statusMessage The HTTP status message (ex: "OK", "Forbidden")
     * @param content       The response content as a string
     * @param headers       The response headers
     * @return A mocked StringResponse
     */
    public static StringResponse mockStringResponse(int statusCode, String statusMessage, String content, Map<String, String> headers) {
        StringResponse response = new StringResponse();

        try {
            if (content != null && !content.isEmpty()) {
                FieldSetter.setField(response, StringResponse.class.getDeclaredField("content"), content);
            }
            if (headers != null && headers.size() > 0) {
                FieldSetter.setField(response, StringResponse.class.getDeclaredField("headers"), headers);
            }
            if (statusCode >= 100 && statusCode < 600) {
                FieldSetter.setField(response, StringResponse.class.getDeclaredField("statusCode"), statusCode);
            }
            if (statusMessage != null && !statusMessage.isEmpty()) {
                FieldSetter.setField(response, StringResponse.class.getDeclaredField("statusMessage"), statusMessage);
            }
        } catch (NoSuchFieldException e) {
            // This would happen in a testing context: spare the exception throw, the test case will probably fail anyway
            return null;
        }

        return response;
    }

    /**
     * Mock an HTTP Response with the given elements.
     *
     * @param statusCode    The status code (ex: 200)
     * @param statusMessage The status message (ex: "OK")
     * @param content       The response content/body
     * @return A mocked HTTP response
     */
    public static CloseableHttpResponse mockHttpResponse(int statusCode, String statusMessage, String content, Header[] headers) {
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        doReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), statusCode, statusMessage))
                .when(response).getStatusLine();
        doReturn(new StringEntity(content, StandardCharsets.UTF_8)).when(response).getEntity();
        if (headers != null && headers.length >= 1) {
            doReturn(headers).when(response).getAllHeaders();
        } else {
            doReturn(new Header[]{}).when(response).getAllHeaders();
        }
        return response;
    }


    public static final String responseLogin() {
        return "{" +
                "   \"ResponseHeader\": {" +
                "       \"Succeeded\": true," +
                "       \"Errors\": null" +
                "   }," +
                "   \"SessionId\": \"9b358c4a-1237-46a7-8167-b62f66dd4a8d\"" +
                "}";
    }

    public static final String responseLoginKO() {
        return
            "{" +
                "\"ResponseHeader\": {" +
                "\"Succeeded\": false," +
                "\"Errors\": [" +
                     "{" +
                        "\"ErrorCode\": \"700\"," +
                        "\"Message\": \"Invalid credentials\"," +
                        "\"AdditionalInfo\": \"\"" +
                     "}" +
                   "]" +
                "}," +
                "\"SessionId\": null" +
            "}";
    }

    public static final String responseInitiate() {
        return
        "{" +
                "\"CheckoutUrl\": \"https://checkout.sandbox.splitit.com/v2/?token=2b7cd23f-2c55-4353-a206-34925a42aedd&culture=en-US\"," +
                "\"ApprovalUrl\": \"https://landing.sandbox.splitit.com/en-US/InstallmentPlan/ShowAgreement/?PublicToken=2b7cd23f-2c55-4353-a206-34925a42aedd\"," +
                "\"TermsAndConditionsUrl\": \"https://www.splitit.com/legals/splitit-shopper-terms-conditions/\"," +
                "\"InstallmentPlanInfoUrl\": \"https://landing.sandbox.splitit.com/en-US/InstallmentPlan/ShowAgreement/?PublicToken=2b7cd23f-2c55-4353-a206-34925a42aedd\"," +
                "\"PublicToken\": \"2b7cd23f-2c55-4353-a206-34925a42aedd\"," +
                "\"ResponseHeader\": {" +
                    "\"Succeeded\": true," +
                    "\"Errors\": []" +
                "}," +
                "\"InstallmentPlan\": {" +
                    "\"InstallmentPlanNumber\": \"36718353567647855177\"," +
                    "\"InstallmentPlanStatus\": {" +
                        "\"Code\": \"Initializing\"," +
                        "\"Id\": 11," +
                        "\"Description\": \"Initializing\"" +
                    "}," +
                    "\"Amount\": {" +
                        "\"Value\": 5.0," +
                        "\"Currency\": {" +
                            "\"Symbol\": \"US$\"," +
                            "\"Id\": 1," +
                            "\"Code\": \"USD\"," +
                            "\"Description\": \"US Dollar\"" +
                        "}" +
                    "}," +
                    "\"OutstandingAmount\": {" +
                        "\"Value\": 5.0," +
                        "\"Currency\": {" +
                            "\"Symbol\": \"US$\"," +
                            "\"Id\": 1," +
                            "\"Code\": \"USD\"," +
                            "\"Description\": \"US Dollar\"" +
                        "}" +
                    "}," +
                    "\"NumberOfInstallments\": 2," +
                    "\"NumberOfProcessedInstallments\": 0," +
                    "\"OriginalAmount\": {" +
                        "\"Value\": 5.0," +
                        "\"Currency\": {" +
                            "\"Symbol\": \"US$\"," +
                            "\"Id\": 1," +
                            "\"Code\": \"USD\"," +
                            "\"Description\": \"US Dollar\"" +
                        "}" +
                    "}," +
                    "\"RefundAmount\": {" +
                        "\"Value\": 0.0," +
                        "\"Currency\": {" +
                            "\"Symbol\": \"US$\"," +
                            "\"Id\": 1," +
                            "\"Code\": \"USD\"," +
                            "\"Description\": \"US Dollar\"" +
                        "}" +
                    "}," +
                    "\"Consumer\": {" +
                        "\"Id\": \"0\"," +
                        "\"UserName\": \"XXZ5P\"," +
                        "\"FullName\": \"John Smith\"," +
                        "\"Email\": \"JohnS@splitit.com\"," +
                        "\"PhoneNumber\": \"1-844-775-4848\"," +
                        "\"CultureName\": \"en-US\"," +
                        "\"RoleName\": null," +
                        "\"IsLocked\": false," +
                        "\"IsDataRestricted\": false" +
                    "}," +
                    "\"ActiveCard\": null," +
                    "\"FraudCheck\": null," +
                    "\"Terminal\": {" +
                        "\"Id\": 30675," +
                        "\"Code\": \"30553\"," +
                        "\"Description\": \"monext\"" +
                    "}," +
                    "\"Merchant\": {" +
                        "\"Id\": 30533," +
                        "\"Code\": \"monext\"," +
                        "\"Description\": \"monext\"" +
                    "}," +
                    "\"RefOrderNumber\": \"012AB\"," +
                    "\"PurchaseMethod\": {" +
                        "\"Code\": \"ECommerce\"," +
                        "\"Id\": 3," +
                        "\"Description\": \"E-Commerce\"" +
                    "}," +
                    "\"Strategy\": {" +
                        "\"Code\": \"SecuredPlan\"," +
                        "\"Id\": 1," +
                        "\"Description\": \"Secured\"" +
                    "}," +
                    "\"DelayResolution\": null," +
                    "\"ExtendedParams\": {" +
                        "\"AnyParameterKey1\": \"AnyParameterVal1\"," +
                        "\"AnyParameterKey2\": \"AnyParameterVal2\"" +
                    "}," +
                    "\"IsFullCaptured\": false," +
                    "\"IsChargedBack\": false," +
                    "\"ArePaymentsOnHold\": false," +
                    "\"ScpFundingPercent\": 0.0," +
                    "\"TestMode\": \"None\"," +
                    "\"CreationDateTime\": \"2020-02-21T07:58:20.7411517+00:00\"," +
                    "\"Installments\": [" +
                    "{" +
                        "\"InstallmentNumber\": 1," +
                        "\"Amount\": {" +
                        "\"Value\": 2.5," +
                        "\"Currency\": {" +
                            "\"Symbol\": \"US$\"," +
                            "\"Id\": 1," +
                            "\"Code\": \"USD\"," +
                            "\"Description\": \"US Dollar\"" +
                        "}" +
                    " }," +
                    "\"OriginalAmount\": {" +
                        "\"Value\": 2.5," +
                        "\"Currency\": {" +
                            "\"Symbol\": \"US$\"," +
                            "\"Id\": 1," +
                            "\"Code\": \"USD\"," +
                            "\"Description\": \"US Dollar\"" +
                        "}" +
                    "}," +
                    "\"RefundAmount\": {" +
                        "\"Value\": 0.0," +
                        "\"Currency\": {" +
                            "\"Symbol\": \"US$\"," +
                            "\"Id\": 1," +
                            "\"Code\": \"USD\"," +
                            "\"Description\": \"US Dollar\"" +
                        "}" +
                    "}," +
                    "\"ProcessDateTime\": \"2020-02-21T07:58:20.867411Z\"," +
                    "\"IsRefund\": false," +
                    "\"RequiredCredit\": {" +
                        "\"Value\": 5.0," +
                        "\"Currency\": {" +
                            "\"Symbol\": \"US$\"," +
                            "\"Id\": 1," +
                            "\"Code\": \"USD\"," +
                            "\"Description\": \"US Dollar\"" +
                        "}" +
                    "}," +
                    "\"CreatedDateTime\": \"2020-02-21T07:58:20.8702427Z\"," +
                    "\"Status\": {" +
                        "\"Code\": \"WaitingForProcessDate\"," +
                        "\"Id\": 2," +
                        "\"Description\": \"Waiting for process date\"" +
                    "}," +
                    "\"TransactionResults\": []," +
                    "\"CardDetails\": null," +
                    "\"Result\": null" +
                    "}," +
                    "{" +
                    "\"InstallmentNumber\": 2," +
                    "\"Amount\": {" +
                        "\"Value\": 2.5," +
                        "\"Currency\": {" +
                            "\"Symbol\": \"US$\"," +
                            "\"Id\": 1," +
                            "\"Code\": \"USD\"," +
                            "\"Description\": \"US Dollar\"" +
                        "}" +
                    "}," +
                    "\"OriginalAmount\": {" +
                        "\"Value\": 2.5," +
                        "\"Currency\": {" +
                            "\"Symbol\": \"US$\"," +
                            "\"Id\": 1," +
                            "\"Code\": \"USD\"," +
                            "\"Description\": \"US Dollar\"" +
                        "}" +
                    "}," +
                    "\"RefundAmount\": {" +
                        "\"Value\": 0.0," +
                        "\"Currency\": {" +
                            "\"Symbol\": \"US$\"," +
                            "\"Id\": 1," +
                            "\"Code\": \"USD\"," +
                            "\"Description\": \"US Dollar\"" +
                        "}" +
                    "}," +
                    "\"ProcessDateTime\": \"2020-03-21T07:58:20.8702487Z\"," +
                    "\"IsRefund\": false," +
                    "\"RequiredCredit\": {" +
                        "\"Value\": 2.5," +
                        "\"Currency\": {" +
                            "\"Symbol\": \"US$\"," +
                            "\"Id\": 1," +
                            "\"Code\": \"USD\"," +
                            "\"Description\": \"US Dollar\"" +
                        "}" +
                    "}," +
                    "\"CreatedDateTime\": \"2020-02-21T07:58:20.8702526Z\"," +
                    "\"Status\": {" +
                        "\"Code\": \"WaitingForProcessDate\"," +
                        "\"Id\": 2," +
                        "\"Description\": \"Waiting for process date\"" +
                    "}," +
                    "\"TransactionResults\": []," +
                    "\"CardDetails\": null," +
                    "\"Result\": null" +
                "}" +
                "]," +
                "\"SecureAuthorizations\": null" +
            "}" +
        "}";
    }

    public static final String responseGet() {
        return
        "{" +
                "\"ResponseHeader\": {" +
                    "\"Succeeded\": true," +
                    "\"Errors\": []" +
                "}," +
                "\"PlansList\": [" +
                "{" +
                    "\"InstallmentPlanNumber\": \"81061838427155704844\"," +
                    "\"InstallmentPlanStatus\": {" +
                        "\"Code\": \"InProgress\"," +
                        "\"Id\": 3," +
                        "\"Description\": \"In Progress\"" +
                    "}" +
                "}" +
                "]" +
        "}";
    }

    public static final String responseVerifyPayment() {
        return
                "{" +
                        "\"ResponseHeader\": {" +
                            "\"Succeeded\": true," +
                            "\"Errors\": null" +
                        "}," +
                        "\"IsPaid\": true," +
                        "\"OriginalAmountPaid\": 2000" +
                "}";
    }

}
