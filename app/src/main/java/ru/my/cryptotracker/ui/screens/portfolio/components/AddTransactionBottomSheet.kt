package ru.my.cryptotracker.ui.screens.portfolio.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import ru.my.cryptotracker.model.entities.CoinUiModel
import java.util.Locale
import ru.my.cryptotracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionBottomSheet(
    availableCoins: ImmutableList<CoinUiModel>,
    onDismiss: () -> Unit,
    onSaveTransaction: (coinId: String, amount: Double, price: Double) -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {

    val defaultCoin = remember(availableCoins) { availableCoins.firstOrNull() }
    var selectedCoin by remember { mutableStateOf(defaultCoin) }

    var priceInput by remember {
        val initialPrice = defaultCoin?.let { coin ->
            coin.displayPrice.replace("$", "").replace(",", "").trim().toDoubleOrNull() ?: 0.0
        } ?: 0.0
        mutableStateOf(if (initialPrice > 0.0) "%.2f".format(Locale.US, initialPrice) else "")
    }

    var amountInput by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var isAmountError by remember { mutableStateOf(false) }
    var isPriceError by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.bottom_sheet_add_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            ExposedDropdownMenuBox(
                expanded = isDropdownExpanded,
                onExpandedChange = { isDropdownExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedCoin?.let { "${it.displayTicker.uppercase()} (${it.id.replaceFirstChar { c -> c.uppercase() }})" } ?: stringResource(id = R.string.bottom_sheet_select_coin_placeholder),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(id = R.string.bottom_sheet_select_coin_placeholder)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                    modifier = Modifier
                        .menuAnchor(
                            type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                            enabled = true
                        )
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors()
                )

                ExposedDropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }
                ) {
                    availableCoins.forEach { coin ->
                        DropdownMenuItem(
                            text = { Text("${coin.displayTicker.uppercase()} — ${coin.id.replaceFirstChar { it.uppercase() }}") },
                            onClick = {
                                selectedCoin = coin
                                val rawPrice = coin.displayPrice.replace("$", "").replace(",", "").trim().toDoubleOrNull() ?: 0.0
                                priceInput = "%.2f".format(Locale.US, rawPrice)
                                isDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = amountInput,
                onValueChange = {
                    amountInput = it
                    isAmountError = false
                },
                label = { Text(stringResource(id = R.string.bottom_sheet_amount_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = isAmountError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = priceInput,
                onValueChange = {
                    priceInput = it
                    isPriceError = false
                },
                label = { Text(stringResource(id = R.string.bottom_sheet_price_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = isPriceError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val coinId = selectedCoin?.id
                    val amount = amountInput.toDoubleOrNull()
                    val price = priceInput.toDoubleOrNull()

                    if (coinId == null) return@Button
                    if (amount == null || amount <= 0.0) { isAmountError = true }
                    if (price == null || price <= 0.0) { isPriceError = true }

                    if (amount != null && amount > 0.0 && price != null && price > 0.0) {
                        onSaveTransaction(coinId, amount, price)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text(text = stringResource(id = R.string.bottom_sheet_btn_save), style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}