package cz.fi.muni.pa165.currency;

import cz.muni.fi.pa165.currency.CurrencyConvertor;
import cz.muni.fi.pa165.currency.CurrencyConvertorImpl;
import cz.muni.fi.pa165.currency.ExchangeRateTable;
import cz.muni.fi.pa165.currency.ExternalServiceFailureException;
import cz.muni.fi.pa165.currency.UnknownExchangeRateException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class CurrencyConvertorImplTest {

    private static Currency CZK = Currency.getInstance("CZK");
    private static Currency EUR = Currency.getInstance("EUR");

    @Mock
    private ExchangeRateTable exchangeRates;

    private CurrencyConvertor converter;

    @Before
    public void setup(){
        converter = new CurrencyConvertorImpl(exchangeRates);
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testConvert() throws ExternalServiceFailureException {
        when(exchangeRates.getExchangeRate(EUR, CZK))
                .thenReturn(new BigDecimal("0.1"));

        assertEquals(new BigDecimal("1.00"), converter
                .convert(EUR, CZK, new BigDecimal("10.050")));

        assertEquals(new BigDecimal("1.01"), converter
                .convert(EUR, CZK, new BigDecimal("10.051")));

        assertEquals(new BigDecimal("1.02"), converter
                .convert(EUR, CZK, new BigDecimal("10.150")));
    }

    @Test
    public void testConvertWithNullSourceCurrency() {
        expectedException.expect(IllegalArgumentException.class);
        converter.convert(null, CZK, BigDecimal.ONE);
        /*assertThatThrownBy(() -> converter.convert(null, CZK, BigDecimal.ONE))
                .isInstanceOf(IllegalArgumentException.class);*/
    }

    @Test
    public void testConvertWithNullTargetCurrency() {
        expectedException.expect(IllegalArgumentException.class);
        converter.convert(EUR, null, BigDecimal.ONE);
    }

    @Test
    public void testConvertWithNullSourceAmount() {
        expectedException.expect(IllegalArgumentException.class);
        converter.convert(EUR, CZK, null);
    }

    @Test
    public void testConvertWithUnknownCurrency() throws ExternalServiceFailureException {
        when(exchangeRates.getExchangeRate(EUR, CZK))
                .thenReturn(null);
        expectedException.expect(UnknownExchangeRateException.class);
        converter.convert(EUR, CZK, BigDecimal.ONE);
    }

    @Test
    public void testConvertWithExternalServiceFailure() throws ExternalServiceFailureException {
        when(exchangeRates.getExchangeRate(EUR, CZK))
                .thenThrow(UnknownExchangeRateException.class);
        expectedException.expect(UnknownExchangeRateException.class);
        converter.convert(EUR, CZK, BigDecimal.ONE);
    }

}
